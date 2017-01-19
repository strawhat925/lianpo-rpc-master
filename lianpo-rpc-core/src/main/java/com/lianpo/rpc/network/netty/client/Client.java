package com.lianpo.rpc.network.netty.client;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.serialize.Serialization;

/**
 * Created by Administrator on 2017/1/5.
 */
public abstract class Client {
    protected String serverAddress;
    protected Serialization serialization;
    protected long timeoutMillis;

    public void init(String serverAddress, Serialization serialization, long timeoutMillis){
        this.serverAddress = serverAddress;
        this.serialization = serialization;
        this.timeoutMillis = timeoutMillis;
    }

    public abstract Response sendRequest(Request request) throws RpcException;

}
