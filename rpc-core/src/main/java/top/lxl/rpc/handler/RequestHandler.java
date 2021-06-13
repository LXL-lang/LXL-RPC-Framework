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

        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());


        return invokeTargetMethod(rpcRequest, service);

    }

    /**
     * 通过反射调用目标方法
     * @param rpcRequest
     * @param service
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
   private Object invokeTargetMethod(RpcRequest rpcRequest,Object service)  {
       Object result;
       try {
         Method  method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
         result=method.invoke(service,rpcRequest.getParameters());
         logger.info("服务：{} 成功调用方法:{}",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
       } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
           return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND,rpcRequest.getRequestId());
       }
       return result;
   }

}
