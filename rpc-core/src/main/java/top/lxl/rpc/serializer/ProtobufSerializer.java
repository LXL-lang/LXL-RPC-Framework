package top.lxl.rpc.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;

import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import top.lxl.rpc.enumeration.SerializerCode;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;



/**
 * @Author : lxl
 * @create : 2021/6/8 23:40
 * @describe: 使用ProtoBuf的序列化器
 * protostuff基于Google Protobuf，好处就是不用自己写.proto文件即可实现对象的序列化与反序列化
    https://blog.csdn.net/zhouzhiwengang/article/details/92794963
 */
public class ProtobufSerializer implements CommonSerializer{
    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
    @Override
    @SuppressWarnings("unchecked")//诉编译器忽略 unchecked 警告信息，如使用List，ArrayList等未进行参数化产生的警告信息
    public byte[] serialize(Object obj) {
        Class clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;

    }
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
    @Override
    public int getCode() {
        return SerializerCode.valueOf("PROTOBUF").getCode();
    }

    @SuppressWarnings("unchecked")
    private Schema getSchema(Class clazz) {
        Schema schema = schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            // 这个schema通过RuntimeSchema进行懒创建并缓存
            // 所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

}
