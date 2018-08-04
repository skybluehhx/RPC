package reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;


/**
 * @author zoujianglin
 * @date 2018/8/1 12:59
 */

@ChannelHandler.Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {
    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;
    private final String host;
    private volatile boolean reconnect = true;
    private int attempts;

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port, String host, boolean reconnect) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("当前链路已经激活重连次数为零");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("链路关闭");
        if (reconnect) {
            System.out.println("链路关闭，将进行重连");
            System.out.println(attempts);
            if (attempts < 12) {
                attempts++;
                int timeout = 2<<attempts;
                timer.newTimeout(this, timeout, TimeUnit.SECONDS);
            }


        }

        ctx.fireChannelInactive();
    }

    public  void  run(Timeout timeout) throws Exception {
        final ChannelFuture future;
        synchronized (bootstrap){
            bootstrap.handler(new ChannelInitializer<Channel>() {
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(handlers());
                }
            });
            future =bootstrap.connect(host,port);

        }
 future.addListener(new ChannelFutureListener() {
     public void operationComplete(ChannelFuture f) throws Exception {
         boolean succed = f.isSuccess();
         if(!succed){
             System.out.println("重连失败");
             f.channel().pipeline().fireChannelInactive();
         }else {
             System.out.println("重连成功");
         }

     }
 });

    }


}
