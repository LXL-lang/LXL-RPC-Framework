package top.lxl.rpc.exception;

import top.lxl.rpc.enumeration.RpcError;

/**
 * @Author : lxl
 * @create : 2021/4/29 22:24
 * @describe:
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcError error) {
        super(error.getMessage());
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }
}
