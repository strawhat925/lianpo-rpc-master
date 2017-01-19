package com.lianpo.rpc.restful;


import com.google.common.base.Strings;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 * MessageBodyWriters 被 JAX-RS 运行时用来序列化所返回资源
 * MessageBodyReader 的最主要的功能是读取请求 InputStream 并将传入的字节反序列化到一个此资源方法期望的 Java 对象
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class FastJsonProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {
    private static final String UTF_8 = "UTF-8";

    /**
     * 这里一定要放回true，解析才会调用readFrom
     * @param aClass
     * @param type
     * @param annotations
     * @param mediaType
     * @return
     */
    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    /**
     * 将流反序列化对应的对象
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     * @param multivaluedMap
     * @param inputStream
     * @return
     * @throws IOException
     * @throws WebApplicationException
     */
    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            InputStreamReader streamReader = new InputStreamReader(inputStream, UTF_8);
            StringBuilder result = new StringBuilder();
            BufferedReader reader = new BufferedReader(streamReader);
            try{
                String line;
                while (!Strings.isNullOrEmpty(line = reader.readLine())){
                    result.append(line);
                }
            }finally {
                streamReader.close();
                reader.close();
            }

            return JSON.parseObject(result.toString(), type.equals(genericType) ? type : genericType);
        } catch (final IOException ex) {
            throw new RestfulException(ex);
        }
    }

    /**
     * 一定要返回true，解析才会调用writeTo
     * @param aClass
     * @param type
     * @param annotations
     * @param mediaType
     * @return
     */
    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * 将对象写入流中
     * @param object
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     * @param multivaluedMap
     * @param outputStream
     * @throws IOException
     * @throws WebApplicationException
     */
    @Override
    public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        try{
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(JSON.toJSONString(object));
            outputWriter.close();
        }catch (final IOException ex){
            throw new RestfulException(ex);
        }
    }
}
