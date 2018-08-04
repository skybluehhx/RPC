package Remote.client;

import Remote.RPCInfo.Request;
import Remote.RPCInfo.Response;
import Remote.Register.ProviderService;
import Remote.Register.RegisterCenterInvoker;
import Remote.balance.ClusterEngine;
import Remote.balance.ClusterStrategy;
import Remote.endoce.RemotingTransporterDecoder;
import Remote.endoce.RemotingTransporterEncoder;
import Remote.model.RemotingTransporter;
import Remote.server.PRCServiceInvoker.Result;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jboss.netty.channel.*;

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


        Map<String, List<ProviderService>> serviceMetaDataMapConsume = registerCenterInvoker.getServiceMetaDataMap4Consume();
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .handler(new MyChannelHandler());

            String serverName = this.targetInterface.getSimpleName();
            //得到服务列表
            List<ProviderService> list = serviceMetaDataMapConsume.get(serverName);
            //根据策略选择服务器
            ClusterStrategy clusterStrategy = ClusterEngine.queryClusterStrategy(strategy);
            ProviderService providerService = clusterStrategy.select(list);
            if (providerService == null) {
                throw new RuntimeException("非常异常，选择的策略不合理");
            }
            String host = providerService.getIp();
            int port = providerService.getPort();
/*
            ProviderService providerService = new ProviderService();
            providerService.setServerName("hello");
            providerService.setIp("127.0.0.1");
            //   providerService.setStrategy(1);
            providerService.setPort(9999);
*/
            //接着构建传输对象向服务器发送数据；
            Request request = new Request();
            request.setProviderService(providerService);
            request.setArgs(null);
            request.setMethodName("say");
            // request.setModule(short);

            RemotingTransporter remotingTransporter = RemotingTransporter.createRequestTransporter((short) 1, request);

            // 异步链接服务器 同步等待链接成功
            io.netty.channel.ChannelFuture f = b.connect(providerService.getIp(), providerService.getPort()).sync();
            f.channel().writeAndFlush(remotingTransporter);

            // 等待链接关闭
            f.channel().closeFuture().sync();
            //等待数据读取完毕
            this.countDownLatch.await();
            System.out.println("返回数据");
            //重新初始化为1 便于后续的使用
            this.countDownLatch = new CountDownLatch(1);

            //返回值
            return result.getResult();

        } finally {
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }


    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{targetInterface}, this);

    }

    public Channel connection() throws Exception {

        return null;
        //  return channel;
    }

    class RemoteHandler extends SimpleChannelHandler {

        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            System.out.println("Client connection");
            super.channelConnected(ctx, e);
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        }

    }

    private class MyChannelHandler extends ChannelInitializer<SocketChannel> {

        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加自定义协议的编解码工具
            ch.pipeline().addLast(new RemotingTransporterEncoder());
            ch.pipeline().addLast(new RemotingTransporterDecoder());
            // 处理网络IO
            ch.pipeline().addLast(new ClientHandler());
        }

    }


    class ClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(io.netty.channel.ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
            RemotingTransporter transporter = (RemotingTransporter) msg;
            Response response = (Response) (transporter.getEnity());

            System.out.println(response.getStatus());
            System.out.println(response.getResult());
            result = response;
            countDownLatch.countDown();


        }


    }


}
