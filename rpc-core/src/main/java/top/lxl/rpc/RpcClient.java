package top.lxl.rpc;

import top.lxl.rpc.entity.RpcRequest;

/**
 * @Author : lxl
 * @create : 2021/6/5 0:12
 * @describe: 客户端类通用接口
 */
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}