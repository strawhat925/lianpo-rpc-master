package com.lianpo.rpc.network.netty.server;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.NetServerFactory;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;

import io.netty.channel.ChannelHandlerContext;

/**
 * message hanlder
 * Created by Administrator on 2017/1/5.
 * @author lianpo
 */
public class MessageHandler implements Runnable {
    private Request request;
    private ChannelHandlerContext channelHandlerContext;

    public MessageHandler(Request request, ChannelHandlerContext channelHandlerContext){
        this.request = request;
        this.channelHandlerContext = channelHandlerContext;
    }
    @Override
    public void run() {
        try{
            Response response = NetServerFactory.invokerServer(request, null);
            channelHandlerContext.writeAndFlush(response);
        }catch (RpcException e){
           //no
        }

    }
}
