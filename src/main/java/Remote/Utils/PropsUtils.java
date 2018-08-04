package Remote.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtils {
    //日志
    private static final Logger LoggER = LoggerFactory.getLogger(PropsUtils.class);

    /**
     * 加载属性文件
     */
    public static Properties loadProps(String fileName) {
        Properties properties = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException(fileName + "filename");

            }
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            LoggER.error("load properties file failure", e);
        } finally {
            if (is != null){
                try {
                    is.close();
                    is=null;
                }catch (Exception e){
                    LoggER.error("close inputstream failure "+e);
                }
            }
        }
        return properties;
    }

    /**
     * 获取字符串属性 默认为空值
     * @param properties
     * @param key
     * @return
     */
    public static String getString(Properties properties, String key){
        return  getString(properties,key,"");
    }

    /**
     *  获取字符型属性 可指定默认值
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Properties properties, String key, String defaultValue){
        String value = defaultValue;

        if(properties.containsKey(key)){
            value = properties.getProperty(key);
        }
        return value;
    }

    /**
     * 获取数值属性 默认为0
     * @param properties
     * @param key
     * @return
     */
    public  static  int getInt(Properties properties, String key){
        return getInt(properties,key,0);
    }
    /***
     * 获取配置文件 数字型 属性
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
     public  static  int getInt(Properties properties, String key, int defaultValue){
        int value = defaultValue;
        if(properties.containsKey(key)){
            value = CastUtil.castInt(properties.getProperty(key));
        }
        return  value;
     }

    /**
     * 获取boolean型属性值，默认为false
     * @param properties
     * @param key
     * @return
     */
     public  static boolean getBoolean(Properties properties, String key){
         return  getBoolean(properties,key,false);
     }
    /**
     * 获取boolean型属性配置 可指定默认值
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
     public static boolean getBoolean(Properties properties, String key, boolean defaultValue){
         boolean value = defaultValue;
         if(properties.containsKey(key)){
             value =CastUtil.castBoolean(properties.getProperty(key));
         }
         return  value;
     }

}
