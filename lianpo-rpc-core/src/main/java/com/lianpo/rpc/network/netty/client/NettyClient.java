package com.lianpo.rpc.network.netty.client;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.network.common.RpcCallbackFuture;
import com.lianpo.rpc.network.netty.server.Channel;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Administrator on 2017/1/5.
 */
public class NettyClient extends Client implements Channel {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    @Override
    public Response sendRequest(Request request) throws RpcException {

        GenericObjectPool<NettyClientPoolProxy> pool = NettyClientPool.getPool(serverAddress, request.getInterfaceName(), serialization, NettyClient.this);
        NettyClientPoolProxy nettyClientPoolProxy = null;

        try {
            RpcCallbackFuture future = new RpcCallbackFuture(request);
            RpcCallbackFuture.futurePool.putIfAbsent(request.getRequestId(), future);

            nettyClientPoolProxy = pool.borrowObject();
            nettyClientPoolProxy.sendRequest(request);

            return future.getResponse(timeoutMillis);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException(e);
        } finally {
            RpcCallbackFuture.futurePool.remove(request.getRequestId());
            if(pool != null){
                pool.returnObject(nettyClientPoolProxy);
            }
        }
    }

}
