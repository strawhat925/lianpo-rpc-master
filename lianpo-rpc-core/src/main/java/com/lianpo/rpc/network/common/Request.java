package com.lianpo.rpc.network.common;

/**
 * request content
 * Created by Administrator on 2017/1/5.
 * @author lianpo
 */
public interface Request {

    String getRequestId();

    byte getVersion();

    String getInterfaceName();

    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getParameters();

}
