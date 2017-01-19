package com.lianpo.rpc.network.netty.server;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.DefaultRpcReqeust;
import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.netty.codec.NettyDecoder;
import com.lianpo.rpc.network.netty.codec.NettyEncoder;
import com.lianpo.rpc.network.netty.transport.MessageHanlder;
import com.lianpo.rpc.serialize.Serialization;
import com.lianpo.rpc.thread.DefaultThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty server
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public class NettyServer extends Server implements Channel {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Thread thread;
    private int port;
    private Serialization serialization;
    private int nThreads;
    private ThreadFactory threadFactory;
    private MessageHanlder messageHanlder;
    private AtomicBoolean active = new AtomicBoolean(false);
    private static Executor executor = Executors.newFixedThreadPool(10);

    private static final EventLoopGroup boss = new NioEventLoopGroup();
    private static final EventLoopGroup worker = new NioEventLoopGroup();
    private io.netty.channel.Channel channel;

    public NettyServer(int port, Serialization serialization, int nThreads) {
        this(port, serialization, nThreads, null);
    }

    public NettyServer(int port, Serialization serialization, int nThreads, MessageHanlder messageHanlder) {
        this.port = port;
        this.serialization = serialization;
        this.nThreads = nThreads;
        this.threadFactory = new DefaultThreadFactory();
        this.messageHanlder = messageHanlder;
    }

    public synchronized void start() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();

                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(boss, worker)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline()
                                            .addLast(new NettyEncoder(serialization, null, DefaultRpcResponse.class))
                                            .addLast(new NettyDecoder(serialization, null, DefaultRpcReqeust.class))
                                            .addLast(new NettyServerHandler(nThreads, threadFactory, messageHanlder, null));
                                }
                            });
                    ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
                    logger.info("Netty server start success, networt={}, port={}", NettyServer.class.getName(), port);
                    io.netty.channel.Channel serviceChannel = future.channel().closeFuture().sync().channel();
                } catch (InterruptedException e) {
                    logger.error("Netty server boot fail", e.getMessage());
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        thread.setName("Netty-Sever-Thread-1");

    }

    @Override
    public void destroy() throws RpcException {
        thread.interrupt();
        logger.info("Netty server destroy success, networt={}", NettyServer.class.getName());
    }

    @Override
    public synchronized boolean open() throws RpcException {

        if(active.get()){
            logger.info("Netty server already open:" + this);
            return true;
        }

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new NettyEncoder(serialization, null, DefaultRpcResponse.class))
                                    .addLast(new NettyDecoder(serialization, null, DefaultRpcReqeust.class))
                                    .addLast(new NettyServerHandler(nThreads, threadFactory, messageHanlder, NettyServer.this));
                        }
                    });
            channel = bootstrap.bind(new InetSocketAddress(port)).sync().channel();
            logger.info("Netty server start success, networt={}, port={}", NettyServer.class.getName(), port);
            //io.netty.channel.Channel serviceChannel = future.channel().closeFuture().sync().channel();
            active.set(true);
        } catch (InterruptedException e) {
            logger.error("Netty server boot fail", e.getMessage());
        } finally {
            //boss.shutdownGracefully();
            //worker.shutdownGracefully();
        }
        return active.get();
    }


    public int getPort() {
        return port;
    }

    public void setMessageHanlder(MessageHanlder messageHanlder) {
        this.messageHanlder = messageHanlder;
    }

    public AtomicBoolean getActive() {
        return active;
    }

    @Override
    public void close() {
        channel.close();
        active.set(false);
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
