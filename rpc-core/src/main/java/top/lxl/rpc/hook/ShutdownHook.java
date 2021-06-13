package top.lxl.rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.util.NacosUtil;
import top.lxl.rpc.factory.ThreadPoolFactory;

/**
 * @Author : lxl
 * @create : 2021/6/10 18:09
 * @describe:
 */
//单列模式
public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
    private static final ShutdownHook shutdownHook=new ShutdownHook();
    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }
    public void addClearAllHook(){
        logger.info("关闭后将自动注销所有服务");
        /* 这个方法的意思就是在jvm中增加一个关闭的钩子，
        当jvm关闭的时候，会执行系统中已经设置的所有通过
        方法addShutdownHook添加的钩子，当系统执行完
        这些钩子后，jvm才会关闭.所以这些钩子可以在
        jvm关闭的时候进行内存清理、对象销毁等操作。
        https://blog.csdn.net/lgshendy/article/details/84736264
         */
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();

        }));
    }

    
}
