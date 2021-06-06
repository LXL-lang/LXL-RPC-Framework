package top.lxl.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lxl.rpc.entity.RpcRequest;
import top.lxl.rpc.entity.RpcResponse;
import top.lxl.rpc.enumeration.SerializerCode;
import top.lxl.rpc.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @Author : lxl
 * @create : 2021/6/6 9:19
 * @describe:
 */
public class KryoSerializer implements CommonSerializer {
    // Kryo 可能存在线程安全问题，文档上是推荐放在 ThreadLocal 里，一个线程一个 Kryo
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    private static final ThreadLocal<Kryo> kryoThreadLocal=ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream);) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }

    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                     Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return  o;
        }catch (Exception e){
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }

    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}