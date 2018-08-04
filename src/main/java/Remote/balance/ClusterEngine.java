package Remote.balance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zoujianglin
 * @date 2018/7/30 19:42
 */
public class ClusterEngine {
    private static final Map<ClusterStrategyEnum, ClusterStrategy> clusterStrategyMap = new ConcurrentHashMap<ClusterStrategyEnum, ClusterStrategy>();

    static {
        clusterStrategyMap.put(ClusterStrategyEnum.Random, new RandomClusterStrategy());
        clusterStrategyMap.put(ClusterStrategyEnum.WeigthRandom, new WeightRandomClusterStrategy());
        clusterStrategyMap.put(ClusterStrategyEnum.Polling, new PollingClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.WeightPolling, new WeightPollingClusterStrategyImpl());

    }

    public static ClusterStrategy queryClusterStrategy(String clusterStrategy) {
        ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.queryByCode(clusterStrategy);
        if (clusterStrategyEnum == null) {
            return new RandomClusterStrategy();
        }
        return clusterStrategyMap.get(clusterStrategyEnum);

    }


}
