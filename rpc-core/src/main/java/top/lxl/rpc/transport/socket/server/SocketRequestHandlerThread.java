package top.lxl.rpc.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.handler.RequestHandler;
import top.lxl.rpc.serializer.CommonSerializer;
import top.lxl.rpc.transport.socket.util.ObjectReader;
import top.lxl.rpc.transport.socket.util.ObjectWriter;

import java.io.*;
import java.net.Socket;

/**
 * @Author : lxl
 * @create : 2021/4/29 23:20
 * @describe: 处理RpcRequest的工作线程
 */
public class SocketRequestHandlerThread implements Runnable{
    private static final Logger logger= LoggerFactory.getLogger(SocketRequestHandlerThread.class);
    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer=serializer;
    }


    @Override
    public void run() {
        try (InputStream inputStream= socket.getInputStream();
             OutputStream outputStream= socket.getOutputStream()
        ){
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream,response,serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
