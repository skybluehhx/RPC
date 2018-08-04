package Remote.Register;

/**
 * 用于封装一个服务消费者的基本信息
 */
public class InvokerService {
    private String invokerName;

    private String IP;

    public String getInvokerName() {
        return invokerName;
    }

    public void setInvokerName(String invokerName) {
        this.invokerName = invokerName;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }


}
