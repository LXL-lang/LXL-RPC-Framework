package top.lxl.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.enumeration.PackageType;
import top.lxl.rpc.serializer.CommonSerializer;

/**
 * @Author : lxl
 * @create : 2021/6/5 15:29
 * @describe:
 */
public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER=0xCAFEBABE;//4 字节魔数，标识一个协议包
    private final CommonSerializer serializer;
    public CommonEncoder(CommonSerializer serializer){
        this.serializer=serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
    //发送魔术
        out.writeInt(MAGIC_NUMBER);
        //发送PackgeType
        if (msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        //发送序列化类型
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        //发送信息长度 防止粘包
        out.writeInt(bytes.length);
        //发送信息
        out.writeBytes(bytes);


    }
}
