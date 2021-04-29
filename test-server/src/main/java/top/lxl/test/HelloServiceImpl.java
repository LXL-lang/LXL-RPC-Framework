package top.lxl.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.api.HelloObject;
import top.lxl.rpc.api.HelloService;

/**
 * @Author : lxl
 * @create : 2021/4/28 18:03
 * @describe:
 */
public class HelloServiceImpl implements HelloService {
    private static final Logger logger= LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到的消息：{}",object.getMessage());
        return "这是调用的返回值，id=" + object.getId();
    }
}
