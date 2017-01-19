package com.lianpo.rpc.network.netty.client;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.netty.server.Channel;
import com.lianpo.rpc.serialize.Serialization;
import com.lianpo.rpc.util.PropertyPlaceholderConfigurer;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2017/1/5.
 * @author lianpo
 */
public class NettyClientPool {

    private GenericObjectPool<NettyClientPoolProxy> pool;
    public NettyClientPool(String host, int port, Serialization serialization, NettyClient nettyClient){

        // 初始化对象池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        ////池对象耗尽之后是否阻塞,maxWait<0时一直等待
        poolConfig.setBlockWhenExhausted(Boolean.parseBoolean(PropertyPlaceholderConfigurer.getContextProperty("netty.blockWhenExhausted")));
        //最大等待时间
        poolConfig.setMaxWaitMillis(Long.parseLong(PropertyPlaceholderConfigurer.getContextProperty(("netty.maxWait"))));
        ////最小空闲
        poolConfig.setMinIdle(Integer.parseInt(PropertyPlaceholderConfigurer.getContextProperty(("netty.minIdle"))));
        //最大空闲
        poolConfig.setMaxIdle(Integer.parseInt(PropertyPlaceholderConfigurer.getContextProperty(("netty.maxIdle"))));
        //最大数
        poolConfig.setMaxTotal(Integer.parseInt(PropertyPlaceholderConfigurer.getContextProperty(("netty.maxTotal"))));
        //取对象是验证
        poolConfig.setTestOnBorrow(Boolean.parseBoolean(PropertyPlaceholderConfigurer.getContextProperty(("netty.testOnBorrow"))));
        //回收验证
        poolConfig.setTestOnReturn(Boolean.parseBoolean(PropertyPlaceholderConfigurer.getContextProperty(("netty.testOnReturn"))));
        //创建时验证
        poolConfig.setTestOnCreate(Boolean.parseBoolean(PropertyPlaceholderConfigurer.getContextProperty(("netty.testOnCreate"))));
        //空闲验证
        poolConfig.setTestWhileIdle(Boolean.parseBoolean(PropertyPlaceholderConfigurer.getContextProperty(("netty.testWhileIdle"))));
        //后进先出
        poolConfig.setLifo(Boolean.parseBoolean(PropertyPlaceholderConfigurer.getContextProperty(("netty.lifo"))));
        pool = new GenericObjectPool<NettyClientPoolProxy>(new NettyClientConnPoolFactory(host, port, serialization, nettyClient), poolConfig);
    }

    public GenericObjectPool<NettyClientPoolProxy> getPool() {
        return pool;
    }

    private static ConcurrentMap<String, NettyClientPool> clientPoolMap = new ConcurrentHashMap<String, NettyClientPool>();
    public static GenericObjectPool<NettyClientPoolProxy> getPool(String serverAddress, String className, Serialization serialization, NettyClient nettyClient) throws RpcException{
        if(serverAddress == null && serverAddress.trim().length() == 0){
            //zk discovery
        }

        if(serverAddress == null && serverAddress.trim().length() == 0){
            throw new RpcException("serverAddress is null");
        }

        NettyClientPool nettyClientPool = clientPoolMap.get(serverAddress);
        if(nettyClientPool != null){
            return nettyClientPool.getPool();
        }

        // init pool
        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        nettyClientPool = new NettyClientPool(host, port, serialization, nettyClient);
        clientPoolMap.put(serverAddress, nettyClientPool);
        return nettyClientPool.getPool();
    }
}
