package top.lxl.test;

import top.lxl.rpc.registry.DefaultServiceRegistry;
import top.lxl.rpc.socket.server.SocketServer;

/**
 * @Author : lxl
 * @create : 2021/4/29 1:04
 * @describe: 测试用服务提供方（服务端）
 */
public class SocketTestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        SocketServer rpcServer = new SocketServer(serviceRegistry);
        rpcServer.start(9000);

    }
}
