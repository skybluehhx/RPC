package Remote.Utils;

/**
 * 类型转换工具 为 PropsUtils服务
 */
public class CastUtil {

    /**
     * 转化为 String 默认值为空
     * @param obj
     * @return
     */
    public  static String castString(Object obj){
        return castString(obj,"");
    }
    /**
     * 转为 String 可指定默认值
     * @param obj
     * @param defaultValue
     * @return
     */
    public  static String castString(Object obj, String defaultValue){
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转化为 int型 默认为0；
     * @param obj
     * @return
     */
    public static  int castInt(Object obj){
        return castInt(obj,0);
    }
    /**
     * 转化为 int型 待默认值
     *
     */
    public  static  int castInt(Object obj, int defaultValue){
        int intValue = defaultValue;
        if(obj !=null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try{
                    intValue= Integer.parseInt(strValue);
                }catch (Exception e){
                    intValue = defaultValue;
                }
            }
        }
        return  intValue;
    }
    /**
     * 转化为 long型 默认为0；
     * @param obj
     * @return
     */
    public static  long castLong(Object obj){
        return castLong(obj,0);
    }

    /**
     * 转为 long型  提供默认值
     * @param obj
     * @param defaultValue
     * @return
     */
    public  static  long castLong(Object obj, long defaultValue){
        long longValue = defaultValue;
        if(obj !=null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try{
                    longValue= Long.parseLong(strValue);
                }catch (Exception e){
                    longValue = defaultValue;
                }
            }
        }
        return  longValue;
    }
    /**
     * 转化为 double型 默认为0；
     * @param obj
     * @return
     */
    public static  double castDouble(Object obj){
        return castDouble(obj,0);
    }
    /**
     * 转为 double型  提供默认值
     * @param obj
     * @param defaultValue
     * @return
     */
    public  static  double castDouble(Object obj, double defaultValue){
        double doubleValue = defaultValue;
        if(obj !=null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try{
                    doubleValue = Double.parseDouble(strValue);
                }catch (Exception e){
                    doubleValue  = defaultValue;
                }
            }
        }
        return  doubleValue ;
    }



    /**
     * 获取boolean 属性 默认值为 false;
     * @param obj
     * @return
     */
    public static boolean castBoolean(Object obj){
        return castBoolean(obj,false);
    }
    /**
     * 获取boolean 型属性 待默认值
     * @param obj
     * @param defultValue
     * @return
     */
    public static  boolean castBoolean(Object obj, Boolean defultValue){
        boolean value = defultValue;
        if(obj != null){
          value = Boolean.parseBoolean(castString(obj));
        }
        return value;
    }

}
