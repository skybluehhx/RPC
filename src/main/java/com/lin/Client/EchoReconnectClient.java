package com.lin.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author zoujianglin
 * @date 2018/8/1 10:48
 */
public class EchoReconnectClient {
    private final String host;
    private final int port;

    public EchoReconnectClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture f=null;
        try {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)

                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("ping",new IdleStateHandler(0,4,0, TimeUnit.SECONDS));
                        p.addLast("decoder", new StringDecoder());
                        p.addLast("encoder", new StringEncoder());
                    //    p.addLast(new HeartBeatClientHandler());

                }
                });
        System.out.println("start connect");
       f = b.connect(host, port).sync();
       // f.channel().writeAndFlush("hello netty server");

      f.channel().closeFuture().sync(); //阻塞在此等待连接关闭
    } finally {
      //  group.shutdownGracefully().sync();
           if(null !=f){
                if(f.channel()!=null &&f.channel().isOpen()){
                    f.channel().close();//关闭当前通道
                }

            }
            System.out.println("准备重新连接");
             start();
            System.out.println("重新连接成功");

    }


}

    public static void main(String[] args) throws Exception {
        System.out.println("1");
        new EchoReconnectClient("localhost", 65535).start();
    }
}
