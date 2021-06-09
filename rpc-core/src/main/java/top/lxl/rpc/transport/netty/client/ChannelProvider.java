package top.lxl.rpc.transport.netty.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.codec.CommonDecoder;
import top.lxl.rpc.codec.CommonEncoder;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;
import top.lxl.rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author : lxl
 * @create : 2021/6/8 0:28
 * @describe: 用于获取 Channel 对象
 */
public class ChannelProvider {
    private static final Logger logger= LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap=initializeBootstrap();
    private static Channel channel=null;
    private static final int MAX_RETRY_COUNT = 5;
    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch=new CountDownLatch(1);
        try {
            connect(bootstrap,inetSocketAddress,countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取channel时有错误发生:", e);
        }
        return channel;
    }
    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }
    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                logger.info("客户端连接成功!");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            //失败重连 https://blog.csdn.net/weixin_42015465/article/details/104229794?spm=1001.2014.3001.5501
            if (retry==0){
                logger.error("客户端连接失败:重试次数已用完，放弃连接！");
                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            //第几次重连
            int order=(MAX_RETRY_COUNT-retry)+1;
            //本次重连的间隔
            int delay=1<<order;
            logger.error("{}：连接失败,第{}次重连...",new Date(),order);
            //定时任务是调用 bootstrap.config().group().schedule()
            //bootstrap.config() 这个方法返回的是 BootstrapConfig
            //bootstrap.config().group() 返回的就是我们在一开始的时候配置的线程模型 workerGroup
            bootstrap.config().group().schedule(()->connect(bootstrap,inetSocketAddress,retry-1,countDownLatch),delay, TimeUnit.SECONDS);
        });
    }
    private static Bootstrap initializeBootstrap(){
        eventLoopGroup=new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间,超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                //是否开启Tcp底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE,true)
                //Tcp默认开启了Nagle算法,该算法的作用是尽可能的发送大数据块,减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY,true);//true的时候金庸 Nagle 算法。
        return bootstrap;

    }
}
