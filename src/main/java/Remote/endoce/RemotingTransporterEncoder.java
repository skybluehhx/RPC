package Remote.endoce;

import Remote.model.RemotingTransporter;
import Remote.seriable.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zoujianglin
 * @date 2018/8/2 9:44
 */
public class RemotingTransporterEncoder extends MessageToByteEncoder<RemotingTransporter> {

    protected void encode(ChannelHandlerContext channelHandlerContext, RemotingTransporter remotingTransporter, ByteBuf byteBuf) throws Exception {
        doEncodeRemotingTransporter(remotingTransporter, byteBuf);
    }

    private void doEncodeRemotingTransporter(RemotingTransporter remotingTransporter, ByteBuf byteBuf) {
        byte[] body = SerializerFactory.getiSerializer().serialize(remotingTransporter.getEnity());
        byteBuf.writeInt(LINProtocol.FLAG) //协议头
                .writeShort(remotingTransporter.getModule())//请求的模块
                .writeByte(remotingTransporter.getTransporterType()) //实体包含的类型
                .writeLong(remotingTransporter.getOpaque())//请求的id
                .writeInt(body.length)//请求的长度
                .writeBytes(body);//请求的实体

    }


}
