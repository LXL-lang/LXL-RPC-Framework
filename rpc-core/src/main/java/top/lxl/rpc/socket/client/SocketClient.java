package top.lxl.rpc.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.RpcClient;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.enumeration.ResponseCode;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Author : lxl
 * @create : 2021/4/28 23:03
 * @describe: 远程方法调用的消费者（客户端）
 * 将一个对象发过去，并且接受返回的对象。
 */
public class SocketClient implements RpcClient{
    private static final Logger logger= LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try(Socket socket=new Socket(host, port)){
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            if (rpcResponse==null){
                logger.error("服务调用失败,service：{}",rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,"service:"+rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode()==null||rpcResponse.getStatusCode()!= ResponseCode.SUCCESS.getCode()){
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        }catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：",e);
            throw new RpcException("服务调用失败: ", e);
        }
    }
}
