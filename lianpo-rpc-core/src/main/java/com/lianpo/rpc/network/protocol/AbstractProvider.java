package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;

/**
 * Created by lianpo on 2017/1/7.
 *
 * @auther lianpo
 */
public abstract class AbstractProvider<T> implements Provider<T>{

    protected Class<?> interfaceClass;
    protected boolean alive = false;
    protected boolean close = false;

    public AbstractProvider(Class<?> interfaceClass){
        this.interfaceClass = interfaceClass;
    }


    @Override
    public Class<?> getInterface() {
        return this.interfaceClass;
    }


    @Override
    public Response call(Request request) {
        Response response = invoker(request);
        return response;
    }

    protected abstract Response invoker(Request request);


    @Override
    public void init() {
        this.alive = true;
    }

    @Override
    public void destroy() {
        this.alive = false;
        this.close = true;
    }

    @Override
    public boolean isAvailable() {
        return this.alive;
    }


}
