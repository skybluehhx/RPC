package Remote.model;

import Remote.endoce.LINProtocol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zoujianglin
 * @date 2018/8/2 9:23
 */

/**
 * 网络传输中的统一对象
 */
public class RemotingTransporter extends ByteHolder {
    private static final AtomicLong requestId = new AtomicLong(0l);

    /**
     * 表示网络传输的类型 有两种 response ,和request
     */
    private byte transporterType;
    /**
     * 标识请求的id
     */
    private long opaque = requestId.getAndIncrement();
    /**
     * 标识请求的模块
     */
    private short module;

    /**
     *传输的对象
     */
    private Object enity;


    public static RemotingTransporter createRequestTransporter(Short module,Object enity){
        RemotingTransporter remotingTransporter = new RemotingTransporter();
     //   remotingTransporter.setCode(code);
     //   remotingTransporter.customHeader = commonCustomHeader;
        remotingTransporter.setModule(module);
        remotingTransporter.transporterType =LINProtocol.REQUEST_REMOTING;
        remotingTransporter.setEnity(enity);
        return remotingTransporter;
    }

    public static RemotingTransporter createResponseTransporter(Long opaque,Object enity){
        RemotingTransporter remotingTransporter = new RemotingTransporter();
       // remotingTransporter.setCode(code);
      // remotingTransporter.customHeader = commonCustomHeader;

       remotingTransporter.setOpaque(opaque);
        remotingTransporter.setEnity(enity);
        remotingTransporter.transporterType = LINProtocol.RESPONSE_REMOTING;
        return remotingTransporter;
    }

    public static RemotingTransporter newInstance(short module,byte transporterType,Long opaque,byte[] bytes){
        RemotingTransporter remotingTransporter = new RemotingTransporter();
        remotingTransporter.setModule(module);
        remotingTransporter.setTransporterType(transporterType);
        remotingTransporter.setOpaque(opaque);
        remotingTransporter.setBytes(bytes);
        return  remotingTransporter;
    }

    public Object getEnity() {
        return enity;
    }

    public void setEnity(Object enity) {
        this.enity = enity;
    }

    public short getModule() {
        return module;
    }

    public void setModule(short module) {
        this.module = module;
    }

    public byte getTransporterType() {
        return transporterType;
    }

    public void setTransporterType(byte transporterType) {
        this.transporterType = transporterType;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }
}
