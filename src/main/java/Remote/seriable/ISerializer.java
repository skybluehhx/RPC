package Remote.seriable;

/**
 * 抽象序列化通用服务服务，所有序列化和反序列的通用接口，用于处理对象序列化的对象
 * 都应该实现该接口
 */
public interface ISerializer {
    /**
     * 序列化
     * @param object
     * @return
     */
    public <T> byte[] serialize(T object);

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T deserialize(byte[] data, Class<T> clazz);

}
