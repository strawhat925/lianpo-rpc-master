package com.lianpo.rpc.network.netty.server;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.netty.transport.MessageHanlder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public abstract class Server {

    public abstract void start() throws RpcException;

    public abstract void destroy() throws RpcException;


    public abstract boolean open() throws RpcException;

    public abstract int getPort();

    public abstract void setMessageHanlder(MessageHanlder messageHanlder);

    public abstract AtomicBoolean getActive();


    public abstract void close();
}
