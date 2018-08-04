package Remote.server;

import Remote.server.PRCServiceInvoker.RPCInvoker;
import Remote.server.annotion.RPCService;
import Remote.server.module.ApplicationHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author zoujianglin
 * @date 2018/7/31 9:50
 */

/**
 * Scanner 主要作用就是 扫描 已经注册的服务 这里
 * 我们定义一个注解 凡是标有该注解的都是
 * 一个服务
 */
@Component
public class Scanner implements BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<? extends Object> clazz = bean.getClass();
        Class<?>[] inters = clazz.getInterfaces();
        if (inters != null && inters.length > 0) {
            for (Class<?> inter : inters) {
                if (inter.isAnnotationPresent(RPCService.class)) {
                    //该类继承有service接口
                    RPCService service = inter.getAnnotation(RPCService.class);
                    String serverName = service.serverName();
                    short module = service.module();

                   Method[] methods = inter.getMethods();
                    for (int i = 0; i < methods.length; i++) {
                        RPCInvoker rpcInvoker = RPCInvoker.valueOf(bean, methods[i]);
                        ApplicationHolder.addRPCInvkoer(module,serverName, methods[i].getName(), rpcInvoker);
                      //  RPCInvokerHoler.addInvoker(module,serverName, methods[i].getName(), rpcInvoker);
                    }

                }
                continue;
            }

        }

        return bean;
    }

    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}
