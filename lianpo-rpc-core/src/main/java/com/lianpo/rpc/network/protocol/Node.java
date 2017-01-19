package com.lianpo.rpc.network.protocol;

/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public interface Node<T> {

    void init();

    void destroy();

    boolean isAvailable();

    String desc();

    String getServiceAddress();
}
