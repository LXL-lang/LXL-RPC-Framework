package top.lxl.test;

import top.lxl.rpc.api.HelloService;
import top.lxl.rpc.serializer.KryoSerializer;
import top.lxl.rpc.serializer.ProtobufSerializer;
import top.lxl.rpc.transport.netty.server.NettyServer;
import top.lxl.rpc.provider.ServiceProviderImpl;
import top.lxl.rpc.provider.ServiceProvider;

/**
 * @Author : lxl
 * @create : 2021/6/5 18:40
 * @describe:
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService=new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1",9999);
        server.setSerializer(new ProtobufSerializer());
        server.publishService(helloService,HelloService.class);
    }
}
