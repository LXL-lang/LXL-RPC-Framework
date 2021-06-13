package top.lxl.test;

import top.lxl.rpc.RpcClient;
import top.lxl.rpc.RpcClientProxy;
import top.lxl.rpc.api.HelloObject;
import top.lxl.rpc.api.HelloService;
import top.lxl.rpc.serializer.CommonSerializer;
import top.lxl.rpc.serializer.KryoSerializer;
import top.lxl.rpc.transport.netty.client.NettyClient;

/**
 * @Author : lxl
 * @create : 2021/6/5 18:42
 * @describe:
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }

}
