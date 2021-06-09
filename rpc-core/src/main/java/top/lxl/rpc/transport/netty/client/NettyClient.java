package top.lxl.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.RpcClient;
import top.lxl.rpc.codec.CommonDecoder;
import top.lxl.rpc.codec.CommonEncoder;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;
import top.lxl.rpc.registry.NacosServiceRegistry;
import top.lxl.rpc.registry.ServiceRegistry;
import top.lxl.rpc.serializer.CommonSerializer;
import top.lxl.rpc.util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @Author : lxl
 * @create : 2021/6/5 1:52
 * @describe: NIO方式消费侧客户端类
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;
    private  CommonSerializer serializer;
    private final ServiceRegistry serviceRegistry;

    static {
        EventLoopGroup group=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true);

    }
    public NettyClient() {
        this.serviceRegistry= new NacosServiceRegistry();
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result=new AtomicReference<>(null);//保证线程安全
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            //同步异步文章 https://blog.csdn.net/weixin_41954254/article/details/106414746
            if (channel.isActive()){
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()){
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    }else   {
                        logger.error("发送消息时由错误发生：", future.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest,rpcResponse);
                result.set(rpcResponse.getData());
            }else {
                System.exit(0);//正常退出 status为0时为正常退出程序，也就是结束当前正在运行中的java虚拟机。
            }

        } catch (InterruptedException e) {
           logger.error("发送消息时有错误发生：",e);
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
