package Remote.TestService;

import Remote.Register.ProviderService;
import Remote.Register.RegisterCenterInvoker;
import Remote.Register.RegisterCenterProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zoujianglin
 * @date 2018/8/3 22:09
 */
public class TestZooker {


    public static  void main(String[] args) {

        RegisterCenterProvider registerCenterProvider = RegisterCenterProvider.getRegisterCenterProvider();
        ProviderService providerService1 = new ProviderService();
        providerService1.setModule((short) 1);
        providerService1.setServerName("hello");
        providerService1.setIp("127.0.0.1");
        providerService1.setPort(8080);

        ProviderService providerService2 = new ProviderService();
        providerService2.setModule((short) 1);
        providerService2.setServerName("hi");
        providerService2.setIp("127.0.0.1");
        providerService2.setPort(8080);
        ProviderService providerService3 = new ProviderService();
        providerService3.setModule((short) 1);
        providerService3.setServerName("yes");
        providerService3.setIp("127.0.0.1");
        providerService3.setPort(8080);
        List<ProviderService> list = new ArrayList<ProviderService>();
        list.add(providerService1);
        list.add(providerService2);
        list.add(providerService3);

        registerCenterProvider.registerProvider(list);

        RegisterCenterInvoker registerCenterInvoker = RegisterCenterInvoker.singleton();
        registerCenterInvoker.initProviderMap();
        Map<String, List<ProviderService>> serverMap= registerCenterInvoker.getServiceMetaDataMap4Consume();
        Set<String> sets= serverMap.keySet();
        for(String serverName:sets){
            System.out.println("服务者的名字"+serverName);
            List<ProviderService> list1= serverMap.get(serverName);
            for(ProviderService providerService:list1){
                System.out.println(providerService.getServerName());

            }



        }




    }
}
