package top.lxl.test;

import top.lxl.rpc.api.HelloObject;
import top.lxl.rpc.api.HelloService;
import top.lxl.rpc.client.RpcClientProxy;

/**
 * @Author : lxl
 * @create : 2021/4/29 0:57
 * @describe: 测试用消费者（客户端）
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is amessage");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
