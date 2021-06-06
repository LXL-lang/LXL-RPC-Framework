package top.lxl.rpc.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.RpcServer;
import top.lxl.rpc.registry.ServiceRegistry;
import top.lxl.rpc.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @Author : lxl
 * @create : 2021/4/29 0:09
 * @describe:  远程方法调用的提供者（服务端）
 */
public class SocketServer implements RpcServer {
    private final ExecutorService threadPool;
    private static final Logger logger= LoggerFactory.getLogger(SocketServer.class);
    private static final int CORE_POOL_SIZE=5;
    private static final int MAXIMUM_POOL_SIZE=50;
    private static final int KEEP_ALIVE_TIME=60;
    private static final int BLOCKING_QUEUE_CAPACITY=100;
    private final ServiceRegistry serviceRegistry;//传入注册表
    private  RequestHandler requestHandler = new RequestHandler();

    public SocketServer(ServiceRegistry serviceRegistry){
        this.serviceRegistry=serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool=new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.SECONDS,workingQueue,threadFactory);
    }
    public void start(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动...");
            Socket socket;
            while ((socket=serverSocket.accept())!=null){
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket,requestHandler,serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
        
    }
}
