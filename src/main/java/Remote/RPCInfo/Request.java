package Remote.RPCInfo;

import Remote.Register.ProviderService;

import java.io.Serializable;

/**
 * @author zoujianglin
 * @date 2018/8/2 14:03
 */
public class Request implements Serializable {


    private short module;

    private ProviderService providerService;

    private Object[] args;

    private String methodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public short getModule() {
        return module;
    }

    public void setModule(short module) {
        this.module = module;
    }

    public ProviderService getProviderService() {
        return providerService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
