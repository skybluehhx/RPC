package Remote.seriable;

/**
 * @author zoujianglin
 * @date 2018/7/31 14:14
 */
public class SerializerFactory {
    public volatile static ISerializer iSerializer;

    private SerializerFactory() {
    }

    public static ISerializer getiSerializer() {
        if (iSerializer == null) {
            synchronized (SerializerFactory.class) {
                if (iSerializer == null) {
                    iSerializer = new HessianSerializer();
                    return iSerializer;
                }
            }

        }
        return iSerializer;
    }


}
