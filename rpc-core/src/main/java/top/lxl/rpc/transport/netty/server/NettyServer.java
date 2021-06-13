package top.lxl.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.RpcServer;
import top.lxl.rpc.codec.CommonDecoder;
import top.lxl.rpc.codec.CommonEncoder;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;
import top.lxl.rpc.hook.ShutdownHook;
import top.lxl.rpc.provider.ServiceProvider;
import top.lxl.rpc.provider.ServiceProviderImpl;
import top.lxl.rpc.registry.NacosServiceRegistry;
import top.lxl.rpc.registry.ServiceRegistry;
import top.lxl.rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author : lxl
 * @create : 2021/6/5 1:28
 * @describe: NIO方式服务提供侧
 */
public class NettyServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final String host;
    private final int port;
    private final CommonSerializer serializer;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public NettyServer(String host,int port){
        this(host,port,DEFAULT_SERIALIZER);
    }
    public NettyServer(String host,int port,Integer serializer){
        this.host=host;
        this.port=port;
        serviceRegistry=new NacosServiceRegistry();
        serviceProvider=new ServiceProviderImpl();
        this.serializer=CommonSerializer.getByCode(serializer);
    }
// publishService，用于向 Nacos 注册服务：
    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if (serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service,serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(),new InetSocketAddress(host,port));
        start();
    }
    @Override
    public void start() {
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))//当添加.addLast("logging", new LoggingHandler(LogLevel.INFO))这行代码时 Netty就会以给定的日志级别打印出LoggingHandler中的日志。 可以对入站出站事件进行日志记录，从而方便我们进行问题排查。
                    .option(ChannelOption.SO_BACKLOG,256)//BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS));
                            pipeline.addLast(new CommonEncoder(serializer));//编码器
                            pipeline.addLast(new CommonDecoder());//解码器
                            pipeline.addLast(new NettyServerHandler());//业务处理器
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host,port).sync();
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            logger.error("启动服务器时有错误发生：",e);
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
