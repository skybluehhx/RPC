package Remote.balance;

/**
 * @author zoujianglin
 * @date 2018/7/30 19:08
 *
 */


import Remote.Register.ProviderService;

import java.util.List;

/**
 * 负载均衡接口
 */
public interface ClusterStrategy {

    public ProviderService select(List<ProviderService> providerServices);
}
