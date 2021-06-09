package top.lxl.test;

import top.lxl.rpc.api.HelloService;
import top.lxl.rpc.serializer.HessianSerializer;
import top.lxl.rpc.transport.socket.server.SocketServer;

/**
 * @Author : lxl
 * @create : 2021/4/29 1:04
 * @describe: 测试用服务提供方（服务端）
 */
public class SocketTestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998);
        socketServer.setSerializer(new HessianSerializer());
        socketServer.publishService(helloService, HelloService.class);

    }
}
