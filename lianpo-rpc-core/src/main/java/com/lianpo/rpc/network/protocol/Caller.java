package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;

/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public interface Caller<T> extends Node<T>{

    Response call(Request request);

    Class<?> getInterface();
}
