package top.lxl.rpc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.enumeration.ResponseCode;
import top.lxl.rpc.provider.ServiceProvider;
import top.lxl.rpc.provider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @Author : lxl
 * @create : 2021/4/29 22:57
 * @describe: 进行过程调用的处理器
 *
 *
 */
public class RequestHandler  {
    private static final Logger logger= LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;
    static {
        serviceProvider = new ServiceProviderImpl();
    }
    public Object handle(RpcRequest rpcRequest){
        Object result = null;
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());

        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务：{} 成功调用方法:{}",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
        } catch (InvocationTargetException|IllegalAccessException e) {
            logger.error("调用或发送时有错误发生：", e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过反射调用目标方法
     * @param rpcRequest
     * @param service
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
   private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws InvocationTargetException, IllegalAccessException {
       Method method;
       try {
           method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
       } catch (NoSuchMethodException e) {
           return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND,rpcRequest.getRequestId());
       }
       return method.invoke(service,rpcRequest.getParameters());
   }

}
