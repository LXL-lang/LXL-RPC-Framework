package top.lxl.rpc;

import top.lxl.rpc.serializer.CommonSerializer;

/**
 * @Author : lxl
 * @create : 2021/6/5 0:13
 * @describe: 服务器类通用接口
 */
public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    void start();
    <T> void publishService(T service,Class<T> serviceClass);
}
