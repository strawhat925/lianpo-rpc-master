package com.lianpo.rpc.network.netty.codec;

import com.lianpo.rpc.serialize.Serialization;
import com.lianpo.rpc.network.codec.Codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by Administrator on 2017/1/5.
 * @author lianpo
 */
public class NettyDecoder extends ByteToMessageDecoder {

    private Serialization serialization;
    private Codec codec;
    private Class<?> genericClass;

    public NettyDecoder(Serialization serialization, Codec codec, Class<?> genericClass){
        this.serialization = serialization;
        this.codec = codec;
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 4){
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if(dataLength < 0){
            channelHandlerContext.close();
        }
        if(byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        Object value = serialization.deserialize(data, genericClass);
        list.add(value);
    }
}
