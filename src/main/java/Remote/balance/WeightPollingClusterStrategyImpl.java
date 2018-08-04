package Remote.balance;

import Remote.Register.ProviderService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zoujianglin
 * @date 2018/7/30 19:32
 */

/**
 * 加权轮询法
 */
public class WeightPollingClusterStrategyImpl implements ClusterStrategy {

    private int index = 0;
    private Lock lock = new ReentrantLock();

    public ProviderService select(List<ProviderService> providerServices) {

        ProviderService providerService = null;
        try {
            lock.tryLock(10, TimeUnit.MILLISECONDS);
            List<ProviderService> providerList = new ArrayList<ProviderService>();
            for (ProviderService providerService1 : providerServices) {
                int weight = providerService1.getWeight();
                for (int i = 0; i < weight; i++) {
                    providerList.add(providerService1);
                }
            }
            if (index >= providerServices.size()) {
                index = 0;
            }
            providerService = providerList.get(index);
            index++;
            return providerService;

        } catch (InterruptedException e) {

        } finally {
            lock.unlock();
        }
        return providerServices.get(0);
    }
}
