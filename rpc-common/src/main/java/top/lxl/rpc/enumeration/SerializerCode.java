package top.lxl.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author : lxl
 * @create : 2021/6/5 16:40
 * @describe: 字节流中标识序列化和反序列化器
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1);


    private final int code;

}
