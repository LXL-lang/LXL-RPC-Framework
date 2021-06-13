package top.lxl.rpc;

import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.serializer.CommonSerializer;

/**
 * @Author : lxl
 * @create : 2021/6/5 0:12
 * @describe: 客户端类通用接口
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER=CommonSerializer.KRYO_SERIALIZER;
    Object sendRequest(RpcRequest rpcRequest);
}
