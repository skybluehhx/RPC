package Remote.TestService;

import Remote.ConfigConstant;
import Remote.Utils.PropsUtils;
import org.junit.Test;

import java.util.Properties;

/**
 * @author zoujianglin
 * @date 2018/8/3 21:23
 */
public class TestConfig {

    @Test
    public void test1() {
        Properties properties = PropsUtils.loadProps(ConfigConstant.CONFIG_FILE);
        PropsUtils.getString(properties, ConfigConstant.INVOKER_TYPE);
        PropsUtils.getInt(properties, ConfigConstant.MODULE);
        PropsUtils.getString(properties, ConfigConstant.PROVIDER_TYPE);
        System.out.println(PropsUtils.getString(properties,ConfigConstant.RPC_ROOT_PATH));
        System.out.println(PropsUtils.getInt(properties,ConfigConstant.ZK_CONNECTION_TIME_OUT));
        System.out.println(PropsUtils.getString(properties,ConfigConstant.ZK_SERVICE));



    }
}
