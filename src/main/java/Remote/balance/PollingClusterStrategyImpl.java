package Remote.balance;

import Remote.Register.ProviderService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zoujianglin
 * @date 2018/7/30 19:25
 */

/**
 * 轮询法
 */
public class PollingClusterStrategyImpl implements ClusterStrategy {
    //计数器
    private AtomicInteger integer = new AtomicInteger(0);

    public ProviderService select(List<ProviderService> providerServices) {
        int MAX_LENGTH = providerServices.size();
        ProviderService providerService = null;
        if (integer.get() >= MAX_LENGTH) {
            integer.set(0);
        }
        providerService = providerServices.get(integer.get());
        integer.getAndIncrement();
        return providerService;

    }
}
