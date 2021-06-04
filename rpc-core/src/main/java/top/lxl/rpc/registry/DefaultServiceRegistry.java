package top.lxl.rpc.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author : lxl
 * @create : 2021/4/29 21:49
 * @describe:
 */
public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger= LoggerFactory.getLogger(DefaultServiceRegistry.class);
    //final修饰是引用的对象不能变serviceMap只能指向当前引用的对象
    private final Map<String,Object> serviceMap=new ConcurrentHashMap<>();

//    NewKeySet:都说是相当于并行集合，相比较于keyset多了add方法。函数得到的set是空的。
    private final Set<String> registeredService=ConcurrentHashMap.newKeySet();

//  getName()返回的是Class在JVM中的名字，而getCanonicalName()返回的Specifications名字可读性更友好，对于普通对象类两者没有区别

    @Override
    public synchronized  <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
//       getInterfaces()方法和Java的反射机制有关。它能够获得这个对象所实现的所有接口。
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length==0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(),service);
        }
        logger.info("向接口：{} 注册服务：{}",interfaces,serviceName);
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);//得到注册的service服务
        if (service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }

        return service;
    }

}
