package top.lxl.rpc.transport.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.factory.SingletonFactory;
import top.lxl.rpc.handler.RequestHandler;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.factory.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;


/**
 * @Author : lxl
 * @create : 2021/6/5 17:30
 * @describe: Netty中处理RpcRequest的Handler
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

   public final RequestHandler requestHandler;
   public NettyServerHandler(){
       this.requestHandler= SingletonFactory.getInstance(RequestHandler.class);
   }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        //为什么用线程池 https://www.cnblogs.com/falcon-fei/p/11422376.html
        ////引入异步业务线程池的方式，避免长时间业务耗时业务阻塞netty本身的worker工作线程

        try {
            if (msg.getHeartBeat()){
                logger.info("接收到客户端的心跳包...");
                return;
            }
            logger.info("服务端接收到请求：{}",msg);
            Object result = requestHandler.handle(msg);
            if (ctx.channel().isActive()&&ctx.channel().isWritable()){
                ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            }else {
                logger.error("通道不可写");
            }
        }finally {
            ReferenceCountUtil.release(msg);//从InBound里读取的ByteBuf要手动释放，还有自己创建的ByteBuf要自己负责释放。这两处要调用这个release方法。 write Bytebuf到OutBound时由netty负责释放，不需要手动调用release
        }



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state==IdleState.READER_IDLE){
                logger.info("长时间为收到心跳包,断开连接...");
                ctx.close();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }
}
