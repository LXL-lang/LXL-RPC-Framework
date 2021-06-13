package top.lxl.rpc.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author : lxl
 * @create : 2021/6/8 21:27
 * @describe: 创建 ThreadPool(线程池) 的工具类
 */
public class ThreadPoolFactory {
    /**
     * 线程池参数
     */
    private static final int CPRE_POOL_SIZE=10;
    private static final int MAXIMUM_POOL_SIZE_SIZE=100;
    private static final int KEEP_ALIVE_TIME=1;
    private static final int BLOCKING_QUEUE_CAPACITY=100;

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static Map<String,ExecutorService> threadPollsMap=new ConcurrentHashMap<>();
    public ThreadPoolFactory() {
    }
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix){
        return createDefaultThreadPool(threadNamePrefix,false);
    }
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix,Boolean daemon){
        //https://www.runoob.com/java/java-hashmap-computeifabsent.html
        //若从map中根据key获取value，如果key不存在，则添加
        ExecutorService pool = threadPollsMap.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadNamePrefix, daemon));
//        isShutDown当调用shutdown()或shutdownNow()方法后返回为true。
//        isTerminated当调用shutdown()方法后，并且所有提交的任务完成后返回为true;
        if (pool.isShutdown()||pool.isTerminated()){
            threadPollsMap.remove(threadNamePrefix);
            pool=createThreadPool(threadNamePrefix,daemon);
            threadPollsMap.put(threadNamePrefix,pool);

        }
        return pool;
    }
    public static void shutDownAll(){
        logger.info("关闭所有的线程池...");
        threadPollsMap.entrySet().parallelStream().forEach(entry->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("关闭线程池 [{}] [{}]",entry.getKey(),executorService.isTerminated());

            //shutdown 方法将执行平缓的关闭过程：不在接受新的任务，同时等待已经提交的任务执行完成---包括那些还未开始执行的任务。
            //shutdownNow 方法将执行粗暴的关闭过程：他将尝试取消所有运行中的任务，并且不再启动队列中尚未开始执行的任务。
            //isShutdown 判断之前是否调用过shutdown
            //isTerminated 判断所有提交的任务是否完成（保证之前调用过shutdown方法）
            //awaitTermination 调用shutdown方法后，等待所有任务执行完成
//            https://my.oschina.net/xinxingegeya/blog/472648?p=1
            try {
                //等待关闭线程池执行器的时间大小
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("关闭线程池失败!");
                executorService.shutdownNow();
            }
        });
    }
     private static ExecutorService createThreadPool(String threadNamePrefix,Boolean daemon){
        //使用有界队列
        BlockingQueue<Runnable> workQueue=new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CPRE_POOL_SIZE,MAXIMUM_POOL_SIZE_SIZE,KEEP_ALIVE_TIME,TimeUnit.MINUTES,workQueue,threadFactory);
    }

    /**
     * 创建 ThreadFactory。如果threadNamePrefix不为空则使用自建的ThreadFactory,否则使用defaultThreadFactory
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon 指定是否为Daemon Thread(守护线程)
     * @return
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix!=null){
            if (daemon!=null){
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            }else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
