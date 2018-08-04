package Remote.Register;

import java.util.List;
import java.util.Map;

/**
 * 消费端注册中心
 */
public interface IRegisterCenterInvoker {

    /**
     * 由客户端启动首次从zookeeper中首次拉去信息
     */
    public void initProviderMap();

    /**
     * 消费端获取服务提供者信息
     * @return
     */
    public Map<String,List<ProviderService>> getServiceMetaDataMap4Consume();

    public void registerInvoker(final InvokerService invoker);

}
