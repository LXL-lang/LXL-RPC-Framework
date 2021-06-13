package top.lxl.rpc.transport.netty.client;

import org.omg.CORBA.PUBLIC_MEMBER;
import top.lxl.rpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : lxl
 * @create : 2021/6/11 22:47
 * @describe:
 */
public class UnprocessedRequests {
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures=new ConcurrentHashMap<>();
    public void put(String requestId,CompletableFuture<RpcResponse> future){
        unprocessedResponseFutures.put(requestId,future);
    }
    public void remove(String requestId){
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse){
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (null!=future){
            future.complete(rpcResponse);
        }else {
            throw new IllegalStateException();
        }
    }
}
