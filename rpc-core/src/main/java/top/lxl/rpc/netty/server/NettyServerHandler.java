package top.lxl.rpc.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.RequestHandler;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.registry.DefaultServiceRegistry;
import top.lxl.rpc.registry.ServiceRegistry;

/**
 * @Author : lxl
 * @create : 2021/6/5 17:30
 * @describe:
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;
    static {
        requestHandler=new RequestHandler();
        serviceRegistry=new DefaultServiceRegistry();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
       try {
           logger.info("服务端接收到请求：{}",msg);
           String interfaceName = msg.getInterfaceName();
           Object service = serviceRegistry.getService(interfaceName);
           Object result = requestHandler.handle(msg, service);
           ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
           future.addListener(ChannelFutureListener.CLOSE);
       }finally {
           ReferenceCountUtil.release(msg);
       }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
