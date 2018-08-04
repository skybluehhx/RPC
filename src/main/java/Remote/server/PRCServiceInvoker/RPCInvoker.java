package Remote.server.PRCServiceInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zoujianglin
 * @date 2018/7/31 9:38
 */
public class RPCInvoker {
    /**
     * 目标对象
     */
    private Object target;

    private Method method;

    private RPCInvoker() {
    }

    ;

    public static RPCInvoker valueOf(Object target, Method method) {
        RPCInvoker invoker = new RPCInvoker();
        invoker.setMethod(method);
        invoker.setTarget(target);
        return invoker;
    }

    public Object invoke(Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
           // return  new RuntimeException("非法参数异常");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            //return  new RuntimeException("调用异常参数异常");
        }
        return null;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
