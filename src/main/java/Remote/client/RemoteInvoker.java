package Remote.client;

import Remote.RPCInfo.Request;
import Remote.RPCInfo.Response;
import Remote.Register.ProviderService;
import Remote.Register.RegisterCenterInvoker;
import Remote.balance.ClusterEngine;
import Remote.balance.ClusterStrategy;
import Remote.endoce.LINProtocol;
import Remote.endoce.RemotingTransporterDecoder;
import Remote.endoce.RemotingTransporterEncoder;
import Remote.model.RemotingTransporter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jboss.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author zoujianglin
 * @date 2018/7/30 21:33
 */

public class RemoteInvoker implements InvocationHandler {

    private RegisterCenterInvoker registerCenterInvoker;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    /**
     * 应用
     */
    private short module;

    /**
     * 服务接口名
     */
    private Class<?> targetInterface;
    /**
     * 负载均衡策略
     */
    private String strategy;

    private Response result;

    public RemoteInvoker(short module, Class<?> targetInterface, String strategy) {
        this.module = module;
        this.targetInterface = targetInterface;
        this.strategy = strategy;
        this.registerCenterInvoker = RegisterCenterInvoker.singleton();
        registerCenterInvoker.initProviderMap();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        ProviderService providerService = getProviderService();
        if (providerService == null) {
            throw new RuntimeException("非法异常，选择的策略不合理或没有该服务");
        }

        Request request = getRequest(providerService, method);
        //接着构建传输对象向服务器发送数据；
        RemotingTransporter remotingTransporter = RemotingTransporter.createRequestTransporter((short) 1, request);

        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .handler(new MyChannelHandler());
            // 异步链接服务器 同步等待链接成功
            io.netty.channel.ChannelFuture f = b.connect(providerService.getIp(), providerService.getPort()).sync();
           f.channel().writeAndFlush(remotingTransporter);

            // 等待链接关闭
            f.channel().closeFuture().sync();
            //等待数据读取完毕
           this.countDownLatch.await();
            //重新初始化为1 便于后续的使用
            this.countDownLatch = new CountDownLatch(1);
            //返回值
            return result.getResult();

        } finally {
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }


    }


    /**
     * 构建具体的请求
     *
     * @return
     */
    private Request getRequest(ProviderService providerService, Method method) {
        Request request = new Request();
        request.setProviderService(providerService);
        request.setArgs(null);
        request.setMethodName(method.getName());
        return request;
    }

    /**
     * 获取服务类
     *
     * @return
     */
    private ProviderService getProviderService() {

        Map<String, List<ProviderService>> serviceMetaDataMapConsume = registerCenterInvoker.getServiceMetaDataMap4Consume();
        String serverName = this.targetInterface.getSimpleName();
        //得到服务列表
        List<ProviderService> list = serviceMetaDataMapConsume.get(serverName);
        //根据策略选择服务器
        ClusterStrategy clusterStrategy = ClusterEngine.queryClusterStrategy(strategy);
        return clusterStrategy.select(list);
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{targetInterface}, this);

    }

    public Channel connection() throws Exception {

        return null;
        //  return channel;
    }


    private class MyChannelHandler extends ChannelInitializer<SocketChannel> {

        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加自定义协议的编解码工具
            ch.pipeline().addLast(new RemotingTransporterEncoder());
            ch.pipeline().addLast(new RemotingTransporterDecoder());
            // 处理网络IO
            ch.pipeline().addLast(new LoginAuthReqHandler());
            ch.pipeline().addLast(new ClientHandler());
        }

    }


    class ClientHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {

            RemotingTransporter transporter = (RemotingTransporter) msg;
            if (transporter.getTransporterType() == LINProtocol.RESPONSE_REMOTING) {
                Response response = (Response) (transporter.getEnity());

                System.out.println(response.getStatus());
                System.out.println(response.getResult());
                result = response;
                countDownLatch.countDown();
            }
            ctx.fireChannelRead(msg);

        }


    }

    /**
     * 开发一个登录认证handler
     */

    class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(io.netty.channel.ChannelHandlerContext ctx) throws Exception {
            System.out.println("203 激活了链路");
            RemotingTransporter remotingTransporter =bulidLoginReq();

            ctx.writeAndFlush(remotingTransporter);
        }
/*
      @Override
        public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
            RemotingTransporter transporter = (RemotingTransporter) msg;
            System.out.println("loginthAuth"+transporter.getTransporterType());
            if (transporter.getTransporterType() == LINProtocol.LOGIN_RESPONSE) {
                byte loginResult = (Byte) transporter.getEnity();
                if (loginResult != (byte) 0) {
                    //握手失败 关闭连接
                    ctx.close();
                } else {
                    System.out.println("login is ok" + "client 2216");

                    ctx.fireChannelRead(msg);

                }

            }
                ctx.fireChannelRead(msg);

        }
*/
        private RemotingTransporter bulidLoginReq() {
            //握手验证，model和byte并没有用到
            RemotingTransporter remotingTransporter = RemotingTransporter.createRequestTransporter((short) 1, 3);
            remotingTransporter.setTransporterType(LINProtocol.LOGIN_REQUEST);
            return remotingTransporter;
        }
    }


}
