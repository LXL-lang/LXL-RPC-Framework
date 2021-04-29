package top.lxl.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @Author : lxl
 * @create : 2021/4/28 21:20
 * @describe: 方法调用的响应状态码
 */

@AllArgsConstructor
@Getter
public enum ResponseCode {
    SUCCESS(200,"调用方法成功!"),
    FAIL(500,"调用方法失败!"),
    METHOD_NOT_FOUND(500,"未找到指定方法"),
    CLASS_NOT_FOUND(500,"未找到指定类");
    private final int code;
    private final String message;
}
