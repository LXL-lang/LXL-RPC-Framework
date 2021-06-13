package top.lxl.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.RpcClient;
import top.lxl.rpc.codec.CommonDecoder;
import top.lxl.rpc.codec.CommonEncoder;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;
import top.lxl.rpc.factory.SingletonFactory;
import top.lxl.rpc.loadbalancer.LoadBalancer;
import top.lxl.rpc.loadbalancer.RandomLoadBalancer;
import top.lxl.rpc.registry.NacosServiceDiscovery;
import top.lxl.rpc.registry.ServiceDiscovery;
import top.lxl.rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;



/**
 * @Author : lxl
 * @create : 2021/6/5 1:52
 * @describe: NIO方式消费侧客户端类
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;
    static {
        group=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);

    }
    private final ServiceDiscovery serviceDiscovery;
    private  CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;
    public NettyClient() {
        this(DEFAULT_SERIALIZER,new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer){
        this(DEFAULT_SERIALIZER,loadBalancer);
    }
    public NettyClient(Integer serializer){
        this(serializer,new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer){
        this.serviceDiscovery=new NacosServiceDiscovery(loadBalancer);
        this.serializer=CommonSerializer.getByCode(serializer);
        this.unprocessedRequests= SingletonFactory.getInstance(UnprocessedRequests.class);
    }
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture=new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //同步异步文章 https://blog.csdn.net/weixin_41954254/article/details/106414746

            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    logger.error("发送消息时由错误发生：", future.cause());
                }
            });
        }catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(),e);
            Thread.currentThread().interrupt();
        }

        return resultFuture;
    }


}
