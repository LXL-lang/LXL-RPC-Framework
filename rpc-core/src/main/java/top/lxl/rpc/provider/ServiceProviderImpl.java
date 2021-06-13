package top.lxl.rpc.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : lxl
 * @create : 2021/4/29 21:49
 * @describe: 默认的服务注册表，保存服务端本地服务
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger= LoggerFactory.getLogger(ServiceProviderImpl.class);
    //final修饰是引用的对象不能变serviceMap只能指向当前引用的对象
    private static final Map<String,Object> serviceMap=new ConcurrentHashMap<>();

//    NewKeySet:都说是相当于并行集合，相比较于keyset多了add方法。函数得到的set是空的。
    private static final Set<String> registeredService=ConcurrentHashMap.newKeySet();

//  getName()返回的是Class在JVM中的名字，而getCanonicalName()返回的Specifications名字可读性更友好，对于普通对象类两者没有区别

    @Override
    public   <T> void addServiceProvider(T service,Class<T> serviceClass) {
        String serviceName = serviceClass.getCanonicalName();
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName,service);
        logger.info("向接口：{} 注册服务：{}",service.getClass().getInterfaces(),serviceName);
    }



    @Override
    public  Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);//得到注册的service服务
        if (service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }

        return service;
    }

}
