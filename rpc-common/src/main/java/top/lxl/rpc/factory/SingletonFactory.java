package top.lxl.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : lxl
 * @create : 2021/6/11 22:53
 * @describe: 单例工厂
 */
public class SingletonFactory {
    private static Map<Class,Object> objectMap=new HashMap<>();
    private SingletonFactory(){}
    public static <T> T getInstance(Class<T> clazz){
        Object instance=objectMap.get(clazz);
        synchronized (clazz){
            try {
                instance=clazz.newInstance();
                objectMap.put(clazz,instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return clazz.cast(instance);
    }
}
