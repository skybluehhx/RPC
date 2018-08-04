package Remote.client;

import Remote.RPCInfo.Request;
import Remote.RPCInfo.Response;
import Remote.Register.ProviderService;
import Remote.endoce.RemotingTransporterDecoder;
import Remote.endoce.RemotingTransporterEncoder;
import Remote.model.RemotingTransporter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author zoujianglin
 * @date 2018/8/2 11:19
 */
public class client {
    /**
     * 连接服务器
     *
     * @param port
     * @param host
     * @throws Exception
     */
  /*
    public void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .handler(new MyChannelHandler());//
            // 异步链接服务器 同步等待链接成功
            ChannelFuture f = b.connect(host, port).sync();


            // 等待链接关闭
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }

    }
*/
    /**
     * 网络事件处理器
     */
    /*
    private class MyChannelHandler extends ChannelInitializer<SocketChannel> {

        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加自定义协议的编解码工具
            ch.pipeline().addLast(new RemotingTransporterEncoder());
            ch.pipeline().addLast(new RemotingTransporterDecoder());
            // 处理网络IO
            ch.pipeline().addLast(new ClientHandler());
        }

    }

    public static void main(String[] args) throws Exception {
        new client().connect(9999, "127.0.0.1");

    }

    class ClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ProviderService providerService = new ProviderService();
            providerService.setServerName("hello");
            providerService.setIp("127.0.0.1");
            providerService.setStrategy(1);
            providerService.setPort(10101);
            Request request =new Request();
            request.setProviderService(providerService);
            request.setArgs(null);
            request.setMethodName("say");
           // request.setModule(short);

            RemotingTransporter remotingTransporter = RemotingTransporter.createRequestTransporter((short) 1,request);
            ctx.writeAndFlush(remotingTransporter);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            RemotingTransporter transporter = (RemotingTransporter) msg;
            Response response = (Response) (transporter.getEnity());

            System.out.println(response.getStatus());
            System.out.println( response.getResult());


        }


    }
    */
}
