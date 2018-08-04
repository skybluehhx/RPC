package Remote.Register;

import java.util.List;
import java.util.Map;

public interface IRegisterCenterProvider
{

    /**
     * 服务端主动拉取客户端信息；
     */
    public void getInvokerMap();
    //服务端将信息注册到zookeeper中
    public void registerProvider(final List<ProviderService> serviceMetaData);
    // 服务端获取所有服务信息
    public Map<String,List<ProviderService>> getProviderSerciceMap();

}
