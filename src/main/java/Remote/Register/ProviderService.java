package Remote.Register;

import java.io.Serializable;

/**
 * @author zoujianglin
 * @date 2018/8/2 11:04
 */
public class ProviderService implements Serializable {

    private short module;
    private String serverName; //服务名
    private String Ip;
    private int port;
    private int strategy;//负载均衡策略
    //默认权重都为1
    private int weight = 1;


    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    public short getModule() {
        return module;
    }

    public void setModule(short module) {
        this.module = module;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
