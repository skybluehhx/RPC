package Remote.balance;

import Remote.Register.ProviderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author zoujianglin
 * @date 2018/7/30 19:20
 */

/**
 * 加权随机法
 */
public class WeightRandomClusterStrategy implements ClusterStrategy {
    public ProviderService select(List<ProviderService> providerServices) {
        //存放加权后的列表
        List<ProviderService> providerList = new ArrayList<ProviderService>();
        for (ProviderService providerService : providerServices) {
            int weight = providerService.getWeight();
            for (int i = 0; i < weight; i++) {
                providerList.add(providerService);
            }
        }
        int MAX_LENGTH = providerList.size();
        Random random = new Random(MAX_LENGTH);
        int index = random.nextInt(MAX_LENGTH);
        return    providerList.get(index);



    }
}
