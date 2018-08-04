package Remote.endoce;

import Remote.model.RemotingTransporter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zoujianglin
 * @date 2018/8/2 10:16
 */
public class RemotingTransporterDecoder extends ByteToMessageDecoder {
    final int BASE_LENGTH = 4 + 2 + 1 + 8;

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() >= BASE_LENGTH) {
            if (byteBuf.readableBytes() > 2048) {
                byteBuf.skipBytes(byteBuf.readableBytes());
            }
            int beginReader;
            while (true) {
                beginReader = byteBuf.readerIndex();
                byteBuf.markReaderIndex();
                if (byteBuf.readInt() == LINProtocol.FLAG) {
                    break;
                }
                byteBuf.resetReaderIndex();
                byteBuf.readByte();
                if (byteBuf.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }

            short module = byteBuf.readShort();
            byte transporterType = byteBuf.readByte();
            Long opaque = byteBuf.readLong();
            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length) {
                byteBuf.readerIndex(beginReader);
                return;
            }
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            RemotingTransporter transporter = RemotingTransporter.newInstance(module, transporterType, opaque, bytes);
             //这里将实体化为相应的对象
            list.add(AnalysisRemotingTransport.doAnalysisRemoting(transporter));
        }


    }
}
