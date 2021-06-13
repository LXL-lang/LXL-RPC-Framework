package top.lxl.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;
import top.lxl.rpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author : lxl
 * @create : 2021/6/6 15:56
 * @describe: Nacos服务注册中心
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger= LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName,inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }

    }


}
