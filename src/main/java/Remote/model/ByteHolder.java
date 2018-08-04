package Remote.model;

/**
 * @author zoujianglin
 * @date 2018/8/2 9:23
 */
public class ByteHolder {

    private transient byte[] bytes;


    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int size() {
        return bytes == null ? 0 : bytes.length;
    }

}
