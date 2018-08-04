package Remote.server.module;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zoujianglin
 * @date 2018/8/1 19:22
 */
public class ModuleHolder {

    private short module;
    /**
     * 服务名 对应这一个服务调用者
     */
    private Map<String, ServerHolder> serverHolders = new HashMap<String, ServerHolder>();


    public ModuleHolder(short module) {
        this.module = module;
    }


    public boolean addServerHolder(String serverName, ServerHolder serverHolder) {
        serverHolders.put(serverName, serverHolder);
        return true;
    }

    public ServerHolder getServerHolder(String serverName) {
        return serverHolders.get(serverName);
    }

    public short getModule() {
        return module;
    }


    @Override
    public boolean equals(Object o) {

        if (o != null && o instanceof ModuleHolder) {
            return ((ModuleHolder) o).getModule() == this.module;
        }
        return false;
    }

    @Override
    public int hashCode() {

        return module;
    }
}
