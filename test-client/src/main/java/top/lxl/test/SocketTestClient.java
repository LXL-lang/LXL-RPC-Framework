package top.lxl.test;

import top.lxl.rpc.api.HelloObject;
import top.lxl.rpc.api.HelloService;
import top.lxl.rpc.RpcClientProxy;
import top.lxl.rpc.serializer.KryoSerializer;
import top.lxl.rpc.transport.socket.client.SocketClient;

/**
 * @Author : lxl
 * @create : 2021/4/29 0:57
 * @describe: 测试用消费者（客户端）
 */
public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient();
        client.setSerializer(new KryoSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is amessage");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
