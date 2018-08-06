package Remote.endoce;

/**
 * @author zoujianglin
 * @date 2018/8/2 9:51
 */
public class LINProtocol {

    public static final int FLAG = -3246724;

    /**
     * 发送的是请求信息
     */
    public static final byte REQUEST_REMOTING = 1;

    /**
     * 发送的是响应信息
     */
    public static final byte RESPONSE_REMOTING = 2;
    /**
     * 消息类型是登录请求
     */
    public static final byte LOGIN_REQUEST = 3;
    /**
     * 消息类型是登录响应
     */
    public static final byte LOGIN_RESPONSE = 4;


}
