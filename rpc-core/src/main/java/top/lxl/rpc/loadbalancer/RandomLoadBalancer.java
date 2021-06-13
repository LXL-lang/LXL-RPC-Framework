package top.lxl.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @Author : lxl
 * @create : 2021/6/11 1:04
 * @describe:
 */
public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
