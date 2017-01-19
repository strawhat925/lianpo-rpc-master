package com.lianpo.rpc.network.netty.server;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.netty.transport.MessageHanlder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private Executor executor;
    private MessageHanlder messageHanlder;
    private Channel serverChannel;

    public NettyServerHandler(int nThreads, ThreadFactory threadFactory, MessageHanlder messageHanlder, Channel serverChannel){
        executor = Executors.newFixedThreadPool(nThreads, threadFactory);
        this.messageHanlder = messageHanlder;
        this.serverChannel = serverChannel;
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Request request) throws Exception {
        final long processStartTime = System.currentTimeMillis();
        try{
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    processRequest(channelHandlerContext, request, processStartTime);
                }
            });
        }catch (RejectedExecutionException rejectException){
            DefaultRpcResponse response = new DefaultRpcResponse();
            response.setRequestId(request.getRequestId());
            response.setVersion(request.getVersion());
            response.setException(new RpcException("process thread pool is full,reject"));
        }

    }

    private void processRequest(ChannelHandlerContext ctx, Request request, long processStartTime) {
        Object result = messageHanlder.handle(serverChannel, request);

        DefaultRpcResponse response = null;

        if (result instanceof DefaultRpcResponse) {
            response = (DefaultRpcResponse) result;
            response.setRequestId(request.getRequestId());
            response.setProcessTime(System.currentTimeMillis() - processStartTime);
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("lianpo-rpc provider netty server caught exception", cause);
        ctx.close();
    }
}
