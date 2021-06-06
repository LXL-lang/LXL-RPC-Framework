package top.lxl.test;

import top.lxl.rpc.RpcClientProxy;
import top.lxl.rpc.api.HelloObject;
import top.lxl.rpc.api.HelloService;
import top.lxl.rpc.netty.client.NettyClient;

/**
 * @Author : lxl
 * @create : 2021/6/5 18:42
 * @describe:
 */
public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);

    }

}
