package top.lxl.rpc.provider;

/**
 * @Author : lxl
 * @create : 2021/4/29 21:45
 * @describe: 保存和提供服务实例对象
 * 本地保存注册表的接口
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service,Class<T> serviceClass);


    Object getServiceProvider(String serviceName);
}
