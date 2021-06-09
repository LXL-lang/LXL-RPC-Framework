package top.lxl.rpc.serializer;

/**
 * @Author : lxl
 * @create : 2021/6/5 16:07
 * @describe: 通用的序列化反序列化接口
 */
public interface CommonSerializer {

    static CommonSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }
    byte[] serialize(Object obj);//序列化的方法
    Object deserialize(byte[] bytes, Class<?> clazz);//发序列化方法
    int getCode();
}
