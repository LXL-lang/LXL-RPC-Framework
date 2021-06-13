package top.lxl.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @Author : lxl
 * @create : 2021/6/11 1:09
 * @describe:
 */
public class RoundRobinLoadBalancer implements LoadBalancer{
    private int index=0;
    @Override
    public Instance select(List<Instance> instances) {
        if (index>=instances.size()){
            index%=instances.size();
        }
        return instances.get(index++);
    }
}
