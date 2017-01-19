package com.lianpo.rpc.enums;

import com.lianpo.rpc.network.netty.client.Client;
import com.lianpo.rpc.network.netty.client.NettyClient;
import com.lianpo.rpc.network.netty.server.NettyServer;
import com.lianpo.rpc.network.netty.server.Server;

/**
 * Created by Administrator on 2017/1/5.
 * @author lianpo
 */
public enum NetCommEnum {
    NETTY(NettyServer.class, NettyClient.class, true);

    public final Class<? extends Server> serverClass;
    public final Class<? extends Client> clientClass;
    public final boolean autoMatch;


    NetCommEnum(Class<? extends Server> serverClass, Class<? extends Client> clientClass, boolean autoMatch) {
        this.serverClass = serverClass;
        this.clientClass = clientClass;
        this.autoMatch = autoMatch;
    }


    public static NetCommEnum autoMatch(String name, NetCommEnum defaultEnum){
        for(NetCommEnum item : NetCommEnum.values()){
            if(item.autoMatch && item.name().equalsIgnoreCase(name)){
                return item;
            }
        }
        return defaultEnum;
    }
}
