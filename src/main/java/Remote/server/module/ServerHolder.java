package Remote.server.module;

import Remote.server.PRCServiceInvoker.RPCInvoker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zoujianglin
 * @date 2018/8/1 19:24
 */
public class ServerHolder {

    private String serverName;

    public ServerHolder(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 保存该方法内的方法名和对应得执行器
     */

    private Map<String, RPCInvoker> map = new HashMap<String, RPCInvoker>();


    public boolean addRPCInvoker(String methodName, RPCInvoker rpcInvoker) {
        map.put(methodName, rpcInvoker);
        return true;
    }

    public RPCInvoker getInvoker(String methodName) {
        return map.get(methodName);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Map<String, RPCInvoker> getMap() {
        return map;
    }


}
