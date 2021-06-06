package top.lxl.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author : lxl
 * @create : 2021/6/5 15:38
 * @describe:
 */
@AllArgsConstructor
@Getter
public enum PackageType {
    REQUEST_PACK(0),
    RESPONSE_PACK(1);
    private final int code;
}
