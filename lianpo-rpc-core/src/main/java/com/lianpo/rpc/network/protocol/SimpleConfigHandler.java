package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.network.netty.server.Server;

/**
 * Created by lianpo on 2017/1/9.
 *
 * @auther lianpo
 */
public class SimpleConfigHandler {

    private SimpleConfigHandler(){

    }

    private static class SingletonHolder{
        public static SimpleConfigHandler instance = new SimpleConfigHandler();
    }

    public static SimpleConfigHandler getInstance(){
        return SingletonHolder.instance;
    }

    public <T> Exporter<T> export(Server server, Class<?> clz){
        Provider<T> provider = new DefaultProvider<T>(clz);
        Protocol protocol = ProtocolFactory.getDefaultProtocol("default");
        Exporter<T> exporter = protocol.export(server, provider);
        return exporter;
    }
}
