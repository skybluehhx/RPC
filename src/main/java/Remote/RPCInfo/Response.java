package Remote.RPCInfo;

import java.io.Serializable;

/**
 * @author zoujianglin
 * @date 2018/8/2 14:22
 */
public class Response implements Serializable {
    /**
     * 状态码
     */
    private int status;
    private Object result;

    public Object getResult() {

        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
