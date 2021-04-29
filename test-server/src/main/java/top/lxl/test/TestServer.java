package top.lxl.test;

import top.lxl.rpc.registry.DefaultServiceRegistry;
import top.lxl.rpc.server.RpcServer;

/**
 * @Author : lxl
 * @create : 2021/4/29 1:04
 * @describe:
 */
public class TestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);

    }
}
