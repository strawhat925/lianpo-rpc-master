package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.network.netty.server.Server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public abstract class AbstractExporter<T> extends AbstractNode<T> implements Exporter<T>{
    protected Provider<T> provider;
    //
    protected static ConcurrentMap<String, Server> ipPort2ServerShareChannel = new ConcurrentHashMap<String, Server>();
    protected Server server;
    public AbstractExporter(Provider<T> provider, Server server){
        this.provider = provider;
        addServer(server);
    }

    protected void addServer(Server server){
        String serviceKey = provider.getServiceAddress();
        synchronized (ipPort2ServerShareChannel){
            Server shareServer = ipPort2ServerShareChannel.get(serviceKey);
            if(shareServer == null){
                shareServer = server;
                ipPort2ServerShareChannel.put(serviceKey, shareServer);
            }
            this.server = shareServer;
        }
    }

    @Override
    public Provider<T> getProvider() {
        return provider;
    }

    @Override
    public String desc() {
        return "[" + this.getClass().getSimpleName() + "] serviceAddress = "  + getServiceAddress();
    }

}
