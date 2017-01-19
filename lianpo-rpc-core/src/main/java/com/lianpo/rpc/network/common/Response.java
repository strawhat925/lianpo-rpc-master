package com.lianpo.rpc.network.common;

/**
 * response content
 * Created by Administrator on 2017/1/5.
 * @author lianpo
 */
public interface Response {

    String getRequestId();

    byte getVersion();

    Exception getException();

    Object getValue();

}
