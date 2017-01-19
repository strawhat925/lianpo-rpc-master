package com.lianpo.rpc.network.netty.client;

import com.lianpo.rpc.serialize.Serialization;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by Administrator on 2017/1/5.
 */
public class NettyClientConnPoolFactory extends BasePooledObjectFactory<NettyClientPoolProxy>{
    private String host;
    private int port;
    private Serialization serialization;
    private NettyClient nettyClient;

    public NettyClientConnPoolFactory(String host, int port, Serialization serialization, NettyClient nettyClient){
        this.host = host;
        this.port = port;
        this.serialization = serialization;
        this.nettyClient = nettyClient;
    }

    @Override
    public NettyClientPoolProxy create() throws Exception {
        NettyClientPoolProxy nettyClientPoolProxy = new NettyClientPoolProxy();
        nettyClientPoolProxy.createProxy(host, port, serialization, nettyClient);
        return nettyClientPoolProxy;
    }

    @Override
    public PooledObject<NettyClientPoolProxy> wrap(NettyClientPoolProxy nettyClientProxy) {
        return new DefaultPooledObject<NettyClientPoolProxy>(nettyClientProxy);
    }

    @Override
    public void destroyObject(PooledObject<NettyClientPoolProxy> p) throws Exception {
        NettyClientPoolProxy nettyClientPoolProxy = p.getObject();

        nettyClientPoolProxy.close();
    }

    @Override
    public boolean validateObject(PooledObject<NettyClientPoolProxy> p) {
        NettyClientPoolProxy nettyClientPoolProxy = p.getObject();
        return nettyClientPoolProxy.isValidate();
    }
}
