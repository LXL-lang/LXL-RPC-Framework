package top.lxl.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @Author : lxl
 * @create : 2021/6/11 1:02
 * @describe:
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances);
}
