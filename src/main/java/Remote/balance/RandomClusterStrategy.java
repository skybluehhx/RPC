package Remote.balance;

import Remote.Register.ProviderService;

import java.util.List;
import java.util.Random;

/**
 * @author zoujianglin
 * @date 2018/7/30 19:10
 */

/**
 * 随机法
 */
public class RandomClusterStrategy implements  ClusterStrategy {
    public ProviderService select(List<ProviderService> providerServices) {

        int MAX_LENGTH =providerServices.size();
        Random random = new Random(MAX_LENGTH);
        int index = random.nextInt(MAX_LENGTH);
        return providerServices.get(index);
    }
}
