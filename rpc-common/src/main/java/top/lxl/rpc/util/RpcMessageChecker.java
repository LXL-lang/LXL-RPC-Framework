package top.lxl.rpc.util;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.enumeration.ResponseCode;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;

/**
 * @Author : lxl
 * @create : 2021/6/8 14:40
 * @describe: 检查响应与请求
 */

public class RpcMessageChecker {
    public static final String INTERFACE_NAME="interfaceName";
    private static final Logger logger= LoggerFactory.getLogger(RpcMessageChecker.class);

    public RpcMessageChecker() {
    }

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if (rpcResponse==null){
            logger.error("调用服务失败,serviceName:{}",rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getStatusCode()==null||!rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())){
            logger.error("服务调用失败,serviceName:{},RpcResponse:{}",rpcRequest.getInterfaceName(),rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
    }
}
