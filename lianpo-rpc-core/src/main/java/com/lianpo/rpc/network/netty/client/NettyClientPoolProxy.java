package com.lianpo.rpc.network.netty.client;

import com.lianpo.rpc.network.common.DefaultRpcReqeust;
import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.network.common.RpcCallbackFuture;
import com.lianpo.rpc.network.netty.codec.NettyDecoder;
import com.lianpo.rpc.network.netty.codec.NettyEncoder;
import com.lianpo.rpc.network.netty.transport.MessageHanlder;
import com.lianpo.rpc.serialize.Serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Administrator on 2017/1/5.
 */
public class NettyClientPoolProxy {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientPoolProxy.class);

    private Channel channel;
    public void createProxy(String host, int port, final Serialization serialization, final NettyClient nettyClient) throws InterruptedException {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new NettyEncoder(serialization, null, DefaultRpcReqeust.class))
                                .addLast(new NettyDecoder(serialization, null, DefaultRpcResponse.class))
                                .addLast(new NettyClientHandler(nettyClient, new MessageHanlder() {
                                    @Override
                                    public Object handle(com.lianpo.rpc.network.netty.server.Channel channel, Object message) {
                                        if(message instanceof DefaultRpcResponse){
                                            DefaultRpcResponse response = (DefaultRpcResponse) message;
                                            RpcCallbackFuture future = RpcCallbackFuture.futurePool.get(response.getRequestId());
                                            future.setResponse(response);
                                            RpcCallbackFuture.futurePool.putIfAbsent(response.getRequestId(), future);
                                        }
                                        return null;
                                    }
                                }));
                    }
                });
        this.channel = bootstrap.connect(host, port).sync().channel();
    }

    public Channel getChannel() {
        return channel;
    }


    public boolean isValidate(){
        if(this.channel != null){
            return this.channel.isActive();
        }
        return false;
    }

    public void close(){
        if(this.channel != null){
            if(this.channel.isOpen()){
                this.channel.close();
            }
        }
        logger.info("netty channel close");
    }


    public void sendRequest(Request request){
        this.channel.writeAndFlush(request);
    }
}
