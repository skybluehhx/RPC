package Remote.server.PRCServiceInvoker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zoujianglin
 * @date 2018/7/31 9:41
 */
public class RPCInvokerHoler {
    //假设不存在重载方法
    //String 放着服务全,名称也就是方法全名


    /**
     * 模块名+服务名+方法名 +调用器  三个确定一个
     */
    public static final Map<Short, Map<String, Map<String, RPCInvoker>>> invokers = new HashMap<Short, Map<String, Map<String, RPCInvoker>>>();


    public static void addInvoker(short module, String serverName, String methodName, RPCInvoker rpcInvoker) {
        Map<String, Map<String, RPCInvoker>> map = invokers.get(module);
        if (map == null) { //如果没有该模块
            map = new HashMap<String, Map<String, RPCInvoker>>();

            Map<String, RPCInvoker> map1 = new HashMap<String, RPCInvoker>();
            map1.put(methodName, rpcInvoker);
            map.put(serverName, map1);
            invokers.put(module, map);
        } else { // 存在该模块
            Map<String, RPCInvoker> methodsMap = map.get(serverName);
            if (methodsMap == null) { //如果没有服务模块
                methodsMap = new HashMap<String, RPCInvoker>();
                methodsMap.put(methodName, rpcInvoker);
                map.put(serverName, methodsMap);
                invokers.put(module, map);
            } else { //有服务模块
                methodsMap.put(methodName, rpcInvoker);
                map.put(serverName, methodsMap);
                invokers.put(module, map);
            }

        }

    }

 /*   public static Result getInvoker(Request request) {

        ISerializer hessianSerializer = SerializerFactory.getiSerializer();
        short module = request.getModule();
        InfoRequest infoRequest = hessianSerializer.deserialize(request.getData(), InfoRequest.class);
        //providerService 中有接口信息 接口 方法 为此 可以通过接口信息 进行收取
        //但在 收取时，服务方需要将所有 这样类进行注册 然后从中获取
        ProviderService providerService = infoRequest.getProviderService();
        Object[] args = infoRequest.getArgs();
        //服务名
        String serverName = providerService.getServerName();
        //方法名
        String methodName = infoRequest.getMethodName();
        Result result = new Result();
        result.setInvoker(getInvoker(module, serverName, methodName));
        result.setArgs(args);
        return result;
    }
*/
    public static RPCInvoker getInvoker(short module, String serverName, String methodName) {
        Map<String, Map<String, RPCInvoker>> map = invokers.get(module);
        if (map == null && map.size() <= 0) {
            return null;
        }
        Map<String, RPCInvoker> map1 = map.get(serverName);
        if (map == null && map.size() <= 0) {
            return null;
        }

        return map1.get(methodName);


    }


}
