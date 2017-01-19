package com.lianpo.rpc.network.common;

import com.lianpo.rpc.exception.RpcException;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * rpc future
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public class RpcCallbackFuture {
    public static ConcurrentMap<String, RpcCallbackFuture> futurePool = new ConcurrentHashMap<String, RpcCallbackFuture>();

    private Object lock = new Object();
    private Request request;
    private Response response;
    private boolean isDone = false;

    public RpcCallbackFuture(Request request) {
        this.request = request;
        futurePool.putIfAbsent(request.getRequestId(), this);
    }

    public Response getResponse(long timeoutMillis) throws InterruptedException {
        if (!isDone) {
            synchronized (lock) {
                try {
                    lock.wait(timeoutMillis);
                } catch (InterruptedException e) {
                    throw new RpcException("response timeout:" + timeoutMillis, e);
                }
            }
        }

        if (!isDone) {
            throw new RpcException(MessageFormat.format("lianpo-rpc, netty request timeout at:{0}, request:{1}", System.currentTimeMillis(), request.toString()));
        }

        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
        synchronized (lock) {
            this.isDone = true;
            lock.notifyAll();
        }
    }

}
