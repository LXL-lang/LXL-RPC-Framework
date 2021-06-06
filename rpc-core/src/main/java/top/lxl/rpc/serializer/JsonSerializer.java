package top.lxl.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.enumeration.SerializerCode;

import java.io.IOException;

/**
 * @Author : lxl
 * @create : 2021/6/5 16:11
 * @describe:
 */
public class JsonSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();//jackson提供的对象，可以用转json,和json转对象

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest){
               obj= handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    /*
           这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
           需要重新判断处理
        */

//    RpcRequest 反序列化时，由于其中有一个字段是 Object 数组，
//    在反序列化时序列化器会根据字段类型进行反序列化，而 Object 就是一个十分模糊的类型，
//    会出现反序列化失败的现象，这时就需要 RpcRequest 中的另一个字段 ParamTypes 来获取到
//    Object 数组中的每个实例的实际类，辅助反序列化，这就是 handleRequest() 方法的作用。

//属性声明为 Object 的，就会造成反序列化出错
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;

        for (int i = 0; i <rpcRequest.getParamTypes().length ; i++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i]=objectMapper.readValue(bytes,clazz);
            }
        }
        return rpcRequest;
    }
    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
