package top.lxl.rpc.entity;

import lombok.Data;
import top.lxl.rpc.enumeration.ResponseCode;

import java.io.Serializable;

/**
 * @Author : lxl
 * @create : 2021/4/28 21:13
 * @describe: 提供者执行完成或出错后向消费者返回的结果对象
 */
@Data
public class RpcResponse<T> implements Serializable {
    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data){//第一个<T>是声明参数中的data为T
        RpcResponse<T> response=new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }
    public static RpcResponse fail(ResponseCode code){
        RpcResponse response=new RpcResponse();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
