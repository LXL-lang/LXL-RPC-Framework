package top.lxl.test;

import top.lxl.rpc.server.RpcServer;

/**
 * @Author : lxl
 * @create : 2021/4/29 1:04
 * @describe:
 */
public class TestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService,9000);
    }
}
