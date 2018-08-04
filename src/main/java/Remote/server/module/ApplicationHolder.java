package Remote.server.module;

import Remote.RPCInfo.Request;
import Remote.model.RemotingTransporter;
import Remote.server.PRCServiceInvoker.RPCInvoker;
import Remote.server.PRCServiceInvoker.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zoujianglin
 * @date 2018/8/1 19:43
 */
public class ApplicationHolder {

    public static final Map<Short, ModuleHolder> applicationHolders = new HashMap<Short, ModuleHolder>();


    public static void addModule(Short module, ModuleHolder moduleHolder) {
        applicationHolders.put(module, moduleHolder);
    }

    public static ModuleHolder getModule(Short module) {
        return applicationHolders.get(module);
    }

    public static void addRPCInvkoer(Short module, String serverName, String methodName, RPCInvoker rpcInvoker) {
        ModuleHolder moduleHolder = applicationHolders.get(module);
        if (moduleHolder == null) {
            moduleHolder = new ModuleHolder(module);
            applicationHolders.put(module, moduleHolder);
        }

        ServerHolder serverHolder = moduleHolder.getServerHolder(serverName);
        if (serverHolder == null) {
            serverHolder = new ServerHolder(serverName);
            moduleHolder.addServerHolder(serverName, serverHolder);
        }
        serverHolder.addRPCInvoker(methodName, rpcInvoker);


    }

    public static Object getInvokerResult(RemotingTransporter transporter) {


        short module =  transporter.getModule();
        Request request = (Request) (transporter.getEnity());
        String serverName = request.getProviderService().getServerName();
        String methodName = request.getMethodName();
        Object[] args = request.getArgs();
        RPCInvoker rpcInvoker = getRPCInvoker(module, serverName, methodName);
        if (rpcInvoker == null) {
            return null;
        }

        Result result = new Result();
        result.setInvoker(rpcInvoker);
        result.setArgs(args);
        return result.invoker();

    }

    /*
        public static Result getInvoker(Request request) {
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
            result.setInvoker(getRPCInvoker(module, serverName, methodName));
            result.setArgs(args);
            return result;



        }
        */
    public static RPCInvoker getRPCInvoker(Short module, String serverName, String methodName) {
        ModuleHolder moduleHolder = applicationHolders.get(module);
        if (moduleHolder == null) {
            return null;
        }
        ServerHolder serverHolder = moduleHolder.getServerHolder(serverName);
        if (serverHolder == null) {
            return null;
        }
        return serverHolder.getInvoker(methodName);


    }


}
