package com.lianpo.rpc.network.protocol;


/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public interface Provider<T> extends Caller<T>{

    Class<?> getInterface();
}
