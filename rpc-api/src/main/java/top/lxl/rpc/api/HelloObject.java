package top.lxl.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : lxl
 * @create : 2021/4/28 17:57
 * @describe: 测试用api的实体 传递的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {//implements Serializable需要从客户端传送给服务端
    private Integer id;
    private String message;
}
