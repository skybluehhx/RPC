package Remote.TestService;

import Remote.server.server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zoujianglin
 * @date 2018/7/31 10:49
 */
public class TestServer1 {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        server serverRPC = (server) applicationContext.getBean("server");
        serverRPC.bind(8080);

        //int t= RPCInvokerHoler.invokers.size();

        // serverRPC.start();
       /* Set<String> keys = RPCInvokerHoler.invokers.keySet();
        for (String key:keys){
           Map<String,RPCInvoker> map= RPCInvokerHoler.invokers.get(key);
          Set<String> key1s =map.keySet();
          for(String key1:key1s){
              System.out.println(key1+"===="+ map.get(key1).getMethod().getName());
          }



        }
*/

// serverRPC.start();

    }


}

