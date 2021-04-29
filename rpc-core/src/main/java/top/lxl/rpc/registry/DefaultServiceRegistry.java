package top.lxl.rpc.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : lxl
 * @create : 2021/4/29 21:49
 * @describe:
 */
public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger= LoggerFactory.getLogger(DefaultServiceRegistry.class);
    //final修饰是引用的对象不能变serviceMap只能指向当前引用的对象
    private final Map<String,Object> serviceMap=new ConcurrentHashMap<>();
    private final Set<String> registeredService=ConcurrentHashMap.newKeySet();
    @Override
    public <T> void register(T service) {

    }

    @Override
    public Object getService(String serviceName) {
        return null;
    }
}
