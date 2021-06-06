package top.lxl.test;

import top.lxl.rpc.api.HelloService;
import top.lxl.rpc.netty.server.NettyServer;
import top.lxl.rpc.registry.DefaultServiceRegistry;
import top.lxl.rpc.registry.ServiceRegistry;

/**
 * @Author : lxl
 * @create : 2021/6/5 18:40
 * @describe:
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService=new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}
