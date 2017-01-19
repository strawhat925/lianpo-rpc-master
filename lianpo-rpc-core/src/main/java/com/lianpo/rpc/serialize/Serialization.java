package com.lianpo.rpc.serialize;

import java.io.IOException;

/**
 * serializer inter
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public interface Serialization {


    byte[] serialize(Object value) throws IOException;


    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
