package Remote.Register;

import Remote.ConfigConstant;
import Remote.Utils.PropsUtils;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务调用者注册中心
 */
public class RegisterCenterInvoker implements IRegisterCenterInvoker {

    private static final Properties CONFIG_PROPS = PropsUtils.loadProps("RPC.properties");
    //服务提供者列表：key ：服务提供者接口，value:服务提供者服务方法列表
    private static final Map<String, List<ProviderService>> providerServiceMap
            = new ConcurrentHashMap<String, List<ProviderService>>();
    public static final Map<String, List<ProviderService>> serviceMetaDataMapConsume
            = new ConcurrentHashMap<String, List<ProviderService>>();
    private static volatile ZkClient zkClient = null;
    private static final String FILE_SEPARATE = "/";
    private static String ZK_SERVICE = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.ZK_SERVICE);
    private static int ZK_SESSION_TIME_OUT = PropsUtils.getInt(CONFIG_PROPS, ConfigConstant.ZK_SESSION_TIME_OUT);
    private static int ZK_CONNECTION_TIME_OUT = PropsUtils.getInt(CONFIG_PROPS, ConfigConstant.ZK_CONNECTION_TIME_OUT);

    private static RegisterCenterInvoker RegisterCenterInvoker = new RegisterCenterInvoker();

    private static String ROOT_PATH = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.RPC_ROOT_PATH);
    private static short module = (short) PropsUtils.getInt(CONFIG_PROPS, ConfigConstant.MODULE);
    private static String PROVIDER_TYPE = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.PROVIDER_TYPE);
    private static String INVOKER_TYPE = PropsUtils.getString(CONFIG_PROPS, ConfigConstant.INVOKER_TYPE);

    private RegisterCenterInvoker() {
    }

    public static RegisterCenterInvoker singleton() {
        return RegisterCenterInvoker;
    }

    public void initProviderMap() {
        if (serviceMetaDataMapConsume.isEmpty()) {
            serviceMetaDataMapConsume.putAll(fetchOrUpdateServiceMetaData());
        }
    }

    /**
     * 使用该函数前请确保已经使用initProviderMap()已近从zookeeper拉取消息到本地进行缓存
     *
     * @return
     */
    public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume() {
        return serviceMetaDataMapConsume;
    }

    /**
     * 该函数用于消费者将自身信息注册到zookeeper上
     *
     * @param invoker
     */
    public void registerInvoker(InvokerService invoker) {
        if (invoker == null) {
            return;
        }
        synchronized (RegisterCenterInvoker.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
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
            //创建服务消费者节点
            String consumeName = invoker.getInvokerName();
            exist = zkClient.exists(ROOT_PATH + "/" + consumeName);
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH + "/" + consumeName);
            }
            String consumeNodePath = ROOT_PATH + "/" + consumeName + INVOKER_TYPE;
            exist = zkClient.exists(consumeNodePath);
            if (!exist) {
                zkClient.createPersistent(consumeNodePath);
            }
            //创建当前客户端节点
            String localIp = invoker.getIP();
            String currentConsumeServicePath = consumeNodePath + "/" + localIp;
            exist = zkClient.exists(currentConsumeServicePath);
            if (!exist) {
                zkClient.createEphemeral(currentConsumeServicePath);
            }


        }


    }

    private Map<String, List<ProviderService>> fetchOrUpdateServiceMetaData() {

        final Map<String, List<ProviderService>> providerServiceMap = new ConcurrentHashMap<String, List<ProviderService>>();
        List<ProviderService> providerServices = null;
        //连接zk 加锁防止重复注册。
        synchronized (IRegisterCenterInvoker.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT,
                        ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
            //从这里开始从服务器获取服务提供者列表 为了方便起见我们假设只有一个模块
            String providerPath = ROOT_PATH + FILE_SEPARATE + module;

            //获取根节点下所有的子节点
            List<String> provideServices = zkClient.getChildren(providerPath);
            for (String serviceName : provideServices) {
                //指定服务名下的所有提供者路劲
                String servicePath = providerPath + FILE_SEPARATE + serviceName + PROVIDER_TYPE;
                //所有提供者ip
                List<String> ipPathList = zkClient.getChildren(servicePath);
                for (String ipPath : ipPathList) {
                    String[] ipAndPort = ipPath.split("\\|");
                    String serverIp = ipAndPort[0];
                    String serverPort = ipAndPort[1];
                    //引用型 与初始创的为同一个引用
                    providerServices = providerServiceMap.get(serviceName);
                    if (providerServices == null) {
                        providerServices = new ArrayList<ProviderService>();
                        providerServiceMap.put(serviceName, providerServices);
                    }
                    ProviderService providerService = new ProviderService();
                    providerService.setIp(serverPort);
                    providerService.setIp(serverIp);
                    providerService.setModule(module);

                    providerService.setServerName(serviceName);

                    providerService.setIp(serverIp);
                    providerService.setPort(Integer.parseInt(serverPort));
                    //将服务添加到列表当中
                    providerServices.add(providerService);


                }
                //遍历完后将服务列表添加到其中
                providerServiceMap.put(serviceName, providerServices);
                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                        if (currentChilds == null) {
                            currentChilds = new ArrayList<String>();
                        }
                        List<String> tmp = new ArrayList<String>();
                        for (String ipPort : currentChilds) {
                            tmp.add(ipPort.split("\\|")[0]);
                        }
                        //  System.out.println("父路径"+parentPath);
                        //调用函数重新刷新本地服务器数据
                        refreshServiceMetaDataMap(tmp);

                    }
                });

            }


        }
        return providerServiceMap;
    }


    //  根据ip来判断 一个服务提供者是否失去作用
    public void refreshServiceMetaDataMap(List<String> ipList) {
        if (ipList == null) {
            ipList = new ArrayList<String>();
        }
        Map<String, List<ProviderService>> currentServiceMetaDataMap = new ConcurrentHashMap<String, List<ProviderService>>();
        for (Map.Entry<String, List<ProviderService>> entry : serviceMetaDataMapConsume.entrySet()) {
            String serviceName = entry.getKey();
            List<ProviderService> providerServices = entry.getValue();

            List<ProviderService> currentProviders = currentServiceMetaDataMap.get(serviceName);
            if (currentProviders == null) {
                currentProviders = new ArrayList<ProviderService>();
            }
            //需要全部遍历，因为一台机器可能提供多个服务
            for (ProviderService providerService : providerServices) {
                if (ipList.contains(providerService.getIp())) {
                    currentProviders.add(providerService);
                }
            }
            currentServiceMetaDataMap.put(serviceName, currentProviders);
        }

        //hashMap 函数此时每个对用的serviceName已经对用当前的新服务提供者列表
        //服务名相同，当前currentProviders会覆盖原键对应得值
        serviceMetaDataMapConsume.putAll(currentServiceMetaDataMap);
       /* Set<String> keys = serviceMetaDataMapConsume.keySet();

        for (String key : keys) {
            List<ProviderService> providerServices = RegisterCenterInvoker.serviceMetaDataMapConsume.get(key);

          //  System.out.println("跟新大小" + providerServices.size() + "size");
            for (ProviderService providerService : providerServices) {

                System.out.println(providerService);
            }

        }

*/
    }
}
