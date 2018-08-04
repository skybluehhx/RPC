package Remote.balance;

/**
 * @author zoujianglin
 * @date 2018/7/30 19:43
 */
public enum ClusterStrategyEnum {

    //随机算法
    Random("Random"),

    WeigthRandom("WeightRandom"),
    Polling("Polling"),
    WeightPolling("WeightPolling");


    private String value;

    private ClusterStrategyEnum(String s) {
        this.value = s;
    }

    public static ClusterStrategyEnum queryByCode(String clusterStrategy) {
        if (clusterStrategy != null && "".equals(clusterStrategy) && clusterStrategy.length() > 0) {
            for (ClusterStrategyEnum clusterStrategyEnum : ClusterStrategyEnum.values()) {
                if (clusterStrategyEnum.getClusterStrategyEnum() == clusterStrategy) {
                    return clusterStrategyEnum;
                }
            }
        }
        return null;
    }

    public String getClusterStrategyEnum() {
        return value;
    }

}
