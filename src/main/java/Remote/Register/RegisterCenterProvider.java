package Remote.Register;

import Remote.ConfigConstant;
import Remote.Utils.PropsUtils;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterCenterProvider implements IRegisterCenterProvider {

    //服务提供者列表：key ：服务提供者接口，value:服务提供者服务方法列表
    private static final Map<String, List<ProviderService>> providerServiceMap
            = new ConcurrentHashMap<String, List<ProviderService>>();
    public static final Map<String, List<ProviderService>> serviceMetaDataMapConsume
            = new ConcurrentHashMap<String, List<ProviderService>>();
    private static volatile ZkClient zkClient = null;

    private static final String FILE_SEPARATE = "/";
    private static final Properties CONFIG_PROPS;

    static {
        System.out.println("静态块被调用");
        CONFIG_PROPS = PropsUtils.loadProps("RPC.properties");
        System.out.println(CONFIG_PROPS);
        int a = 0;
    }

    private String ZK_SERVICE = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.ZK_SERVICE);

    private int ZK_SESSION_TIME_OUT = PropsUtils.getInt(CONFIG_PROPS, ConfigConstant.ZK_SESSION_TIME_OUT);

    private int ZK_CONNECTION_TIME_OUT = PropsUtils.getInt(CONFIG_PROPS, ConfigConstant.ZK_CONNECTION_TIME_OUT);

    private String ROOT_PATH = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.RPC_ROOT_PATH);

    private short module = (short) PropsUtils.getInt(CONFIG_PROPS, ConfigConstant.MODULE);

    private String PROVIDER_TYPE = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.PROVIDER_TYPE);

    private String INVOKER_TYPE = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.INVOKER_TYPE);

    private static RegisterCenterProvider registerCenterProvider = new RegisterCenterProvider();

    private RegisterCenterProvider() {

    }

    public static RegisterCenterProvider getRegisterCenterProvider() {

        return registerCenterProvider;
    }

    public void getInvokerMap() {

    }

    /**
     * 注册服务
     *
     * @param serviceMetaData 待注册的服务列表
     */
    public void registerProvider(List<ProviderService> serviceMetaData) {
        if (serviceMetaData == null || serviceMetaData.isEmpty()) {
            return;
        }
        synchronized (RegisterCenterProvider.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT);
            }
            boolean exist = zkClient.exists(ROOT_PATH);
            StringBuilder currentPath = new StringBuilder(ROOT_PATH);
            if (!exist) {
                zkClient.createPersistent(currentPath.toString(), true);
            }
            currentPath.append(FILE_SEPARATE).append(module);
            exist = zkClient.exists(currentPath.toString());
            if (!exist) {
                zkClient.createPersistent(currentPath.toString(), true);
            }

            //创建服务提供者节点
            for (ProviderService providerService : serviceMetaData) {
                String providerName = providerService.getServerName();

                exist = zkClient.exists(currentPath + FILE_SEPARATE + providerName);
                if (!exist) {
                    zkClient.createPersistent(currentPath + FILE_SEPARATE + providerName);
                }
                String providerNodePath = currentPath + FILE_SEPARATE + providerName + PROVIDER_TYPE;
                exist = zkClient.exists(providerNodePath);
                if (!exist) {
                    zkClient.createPersistent(providerNodePath);
                }
                String serverIp = providerService.getIp();
                int serverPort = providerService.getPort();
                String currentNodePath = providerNodePath + FILE_SEPARATE + serverIp + "|" + serverPort;
                exist = zkClient.exists(currentNodePath);
                if (!exist) {
                    zkClient.createPersistent(currentNodePath);
                }

            }

        }


    }

    //获取方式与服务端相似再此略去
    public Map<String, List<ProviderService>> getProviderSerciceMap() {
        return null;
    }
}
