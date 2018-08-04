package Remote.server.PRCServiceInvoker;

/**
 * @author zoujianglin
 * @date 2018/7/31 14:22
 */
public class Result {
    private Object[] args;

    private RPCInvoker invoker;

    public Object invoker() {
        return invoker.invoke(args);
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public RPCInvoker getInvoker() {
        return invoker;
    }

    public void setInvoker(RPCInvoker invoker) {
        this.invoker = invoker;
    }
}
