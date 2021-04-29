package top.lxl.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Author : lxl
 * @create : 2021/4/28 23:03
 * @describe: 远程方法调用的消费者（客户端）
 * 将一个对象发过去，并且接受返回的对象。
 */
public class RpcClient {
    private static final Logger logger= LoggerFactory.getLogger(RpcClient.class);
    public Object sendRequest(RpcRequest rpcRequest,String host,int port){
        try(Socket socket=new Socket(host, port)){
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：",e);
            return null;
        }
    }
}
