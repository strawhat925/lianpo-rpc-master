package com.lianpo.rpc.network.netty.codec;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.codec.Codec;
import com.lianpo.rpc.serialize.Serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {
    private static final Logger logger = LoggerFactory.getLogger(NettyEncoder.class);
    private Serialization serialization;
    private Codec codec;
    private Class<?> genericClass;

    public NettyEncoder(Serialization serialization, Codec codec, Class<?> genericClass) {
        this.serialization = serialization;
        this.codec = codec;
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        // is type instance
        try {
            if (genericClass.isInstance(object)) {
                byte[] data = serialization.serialize(object);
                byteBuf.writeInt(data.length);
                byteBuf.writeBytes(data);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException(e.getMessage());
        }

    }
}
