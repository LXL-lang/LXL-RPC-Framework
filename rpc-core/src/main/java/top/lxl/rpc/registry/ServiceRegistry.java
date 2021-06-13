package top.lxl.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @Author : lxl
 * @create : 2021/6/6 15:56
 * @describe:  服务注册中心通用接口 远程注册表(Nacos)
 */
public interface ServiceRegistry {
    /**
     * 将一个服务注册进注册表
     *
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

}
