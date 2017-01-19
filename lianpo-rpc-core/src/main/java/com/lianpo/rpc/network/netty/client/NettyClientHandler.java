package com.lianpo.rpc.network.netty.client;

import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.network.common.RpcCallbackFuture;
import com.lianpo.rpc.network.netty.server.Channel;
import com.lianpo.rpc.network.netty.server.NettyServerHandler;
import com.lianpo.rpc.network.netty.transport.MessageHanlder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Administrator on 2017/1/5.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private MessageHanlder messageHanlder;
    private Channel serverChannel;
    public NettyClientHandler(Channel serverChannel, MessageHanlder messageHanlder){
        this.messageHanlder = messageHanlder;
        this.serverChannel = serverChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        /*if(response instanceof DefaultRpcResponse){
            RpcCallbackFuture future = RpcCallbackFuture.futurePool.get(response.getRequestId());
            future.setResponse(response);
            RpcCallbackFuture.futurePool.putIfAbsent(response.getRequestId(), future);
        }*/
        messageHanlder.handle(serverChannel, response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("lianpo-rpc provider netty client caught exception", cause);
        ctx.close();
    }
}
