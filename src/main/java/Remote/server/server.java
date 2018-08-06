package Remote.server;

import Remote.RPCInfo.Response;
import Remote.endoce.LINProtocol;
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

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
            //登录认证handler
            ch.pipeline().addLast(new LoginAuthRespHandler());
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
            System.out.println("进入了ServerHanlder 位于server78行");
            System.out.println(transporter.getTransporterType());
            if (transporter.getTransporterType() == LINProtocol.REQUEST_REMOTING) {
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
            ctx.fireChannelRead(msg);

        }
    }


    /**
     * 开发一个握手认证handler
     */
    class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

        private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();

        //允许通过的ip名单，可通过配置文件配置
        private String[] whiteList = {"127.0.0.1", "192.168.1.104"};

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            RemotingTransporter transporter = (RemotingTransporter) msg;
            if (transporter.getTransporterType() == LINProtocol.LOGIN_REQUEST) {
                System.out.println("进入了方法里登录认证的channelRead 位于server 118行");
                String nodeIndex = ctx.channel().remoteAddress().toString();
                RemotingTransporter login = null;
                if (nodeCheck.containsKey(nodeIndex)) {
                    //该ip已经登录，拒绝
                    login = buildResponde(transporter.getOpaque(), -1);
                } else {
                    InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                    String ip = address.getAddress().getHostAddress();
                    boolean isOk = false;
                    //当whiteList长度很大时，考虑使用hashMap存储
                    for (String WIP : whiteList) {
                        if (WIP.equals(ip)) {
                            isOk = true;
                            break;
                        }
                    }
                    login = isOk ? buildResponde(transporter.getOpaque(), (byte) 0) : buildResponde(transporter.getOpaque(), (byte) -1);
                    System.out.println("登录成功，位于server 131行");
                    ctx.writeAndFlush(login);
                }


            }
            //不是握手消息 透传给其他
            ctx.fireChannelRead(msg);
        }

        private RemotingTransporter buildResponde(long opaque, int result) {
            RemotingTransporter remotingTransporter = RemotingTransporter.createResponseTransporter(opaque, result);
            remotingTransporter.setTransporterType(LINProtocol.LOGIN_RESPONSE);
            remotingTransporter.setEnity(result);
            return remotingTransporter;
        }

        /**
         * 异常关闭需要移除，以便后续登录
         *
         * @param ctx
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            nodeCheck.remove(ctx.channel().remoteAddress().toString());
            ctx.close();
            System.out.println("异常");
            System.out.println(cause);
            ctx.fireExceptionCaught(cause);
        }
    }


}
