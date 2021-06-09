package top.lxl.rpc.transport.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.handler.RequestHandler;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.util.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;


/**
 * @Author : lxl
 * @create : 2021/6/5 17:30
 * @describe: Netty中处理RpcRequest的Handler
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;

    private static final ExecutorService threadPool;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    static {
        requestHandler=new RequestHandler();
        threadPool= ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        //为什么用线程池 https://www.cnblogs.com/falcon-fei/p/11422376.html
        ////引入异步业务线程池的方式，避免长时间业务耗时业务阻塞netty本身的worker工作线程
        threadPool.execute(()->{
        try {
            logger.info("服务端接收到请求：{}",msg);
            Object result = requestHandler.handle(msg);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result,msg.getRequestId()));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);//从InBound里读取的ByteBuf要手动释放，还有自己创建的ByteBuf要自己负责释放。这两处要调用这个release方法。 write Bytebuf到OutBound时由netty负责释放，不需要手动调用release
        }
        });


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
