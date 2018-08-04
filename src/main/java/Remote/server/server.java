package Remote.server;

import Remote.RPCInfo.Response;
import Remote.endoce.RemotingTransporterDecoder;
import Remote.endoce.RemotingTransporterEncoder;
import Remote.model.RemotingTransporter;
import Remote.server.module.ApplicationHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;

/**
 * @author zoujianglin
 * @date 2018/8/2 10:47
 */
@Component("server")
public class server {
    public server() {
    }

    public void bind(int port) throws Exception {
        // 配置NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务器辅助启动类配置
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChildChannelHandler())//
                    .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区 // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            // 绑定端口 同步等待绑定成功
            ChannelFuture f = b.bind(port).sync(); // (7)
            // 等到服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅释放线程资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加自定义协议的编解码工具
            ch.pipeline().addLast(new RemotingTransporterEncoder());
            ch.pipeline().addLast(new RemotingTransporterDecoder());
            // 处理网络IO
            ch.pipeline().addLast(new ServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        new server().bind(9999);

    }

    class ServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            RemotingTransporter transporter = (RemotingTransporter) msg;

       //接着通过持有者获取调用结果
            Object result = ApplicationHolder.getInvokerResult(transporter);
            Response response = new Response();
            response.setStatus(200);
            response.setResult(result);
            transporter.getModule();

            /**
             * 构建远程传输对象
             */
            RemotingTransporter remotingTransporte = RemotingTransporter.createResponseTransporter(transporter.getOpaque(), response);
            //返回响应
            ctx.channel().writeAndFlush(remotingTransporte);
            //关闭连接
            ctx.channel().close();


        }
    }


}
