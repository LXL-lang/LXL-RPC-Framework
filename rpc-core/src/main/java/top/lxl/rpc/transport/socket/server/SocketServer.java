package top.lxl.rpc.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.RpcServer;
import top.lxl.rpc.enumeration.RpcError;
import top.lxl.rpc.exception.RpcException;
import top.lxl.rpc.hook.ShutdownHook;
import top.lxl.rpc.provider.ServiceProvider;
import top.lxl.rpc.handler.RequestHandler;
import top.lxl.rpc.provider.ServiceProviderImpl;
import top.lxl.rpc.registry.NacosServiceRegistry;
import top.lxl.rpc.registry.ServiceRegistry;
import top.lxl.rpc.serializer.CommonSerializer;
import top.lxl.rpc.factory.ThreadPoolFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @Author : lxl
 * @create : 2021/4/29 0:09
 * @describe:  远程方法调用的提供者（服务端）
 */
public class SocketServer implements RpcServer {
    private static final Logger logger= LoggerFactory.getLogger(SocketServer.class);
    private final ExecutorService threadPool;
    private final String host;
    private final int port;
    private CommonSerializer serializer;
    private  RequestHandler requestHandler = new RequestHandler();
    private final ServiceProvider serviceProvider;
    private final ServiceRegistry serviceRegistry;

    public SocketServer(String host,int port){
        this(host,port, DEFAULT_SERIALIZER);
    }
    public SocketServer(String host,int port,Integer serializer){
        this.host=host;
        this.port=port;
        threadPool= ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry=new NacosServiceRegistry();
        this.serviceProvider=new ServiceProviderImpl();
        this.serializer=CommonSerializer.getByCode(serializer);
    }

    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if (serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service,serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(),new InetSocketAddress(host,port));
        start();
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host,port));
            logger.info("服务器启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket=serverSocket.accept())!=null){
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket,requestHandler,serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
        
    }



}
