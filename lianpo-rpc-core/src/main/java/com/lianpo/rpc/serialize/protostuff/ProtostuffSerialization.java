package com.lianpo.rpc.serialize.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.lianpo.rpc.serialize.Serialization;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2017/1/6.
 * @author lianpo
 */
public class ProtostuffSerialization implements Serialization{

    private static ConcurrentMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private <T> Schema<T> getSchema(Class<T> clazz){
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if(schema == null){
            schema = RuntimeSchema.getSchema(clazz);
            if(schema != null){
                cachedSchema.put(clazz, schema);
            }
        }
        return schema;
    }

    @Override
    public byte[] serialize(Object value) throws IOException {
        Class clazz = value.getClass();
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(value, schema, linkedBuffer);
        }catch (Exception e){
            throw new IOException(e.getMessage(), e);
        }finally {
            linkedBuffer.clear();
        }

    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        try {
            T value = clazz.newInstance();
            Schema schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, value, schema);
            return value;
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
