package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.netty.server.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lianpo on 2017/1/9.
 *
 * @auther lianpo
 */
public abstract class AbstractProtocol implements Protocol{
    private static final Logger logger = LoggerFactory.getLogger(AbstractProtocol.class);
    protected ConcurrentHashMap<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>();

    public <T> Exporter<T> export(Server server, Provider<T> provider){
        if(provider == null){
            //do
            throw new RpcException("export service cannot be empty.");
        }
        String protocolKey = provider.getServiceAddress() + provider.getInterface();
        synchronized (exporterMap){
            Exporter<T> exporter = (Exporter<T>) exporterMap.get(protocolKey);
            if(exporter != null){
                throw new RpcException("export service cannot be repeated.");
            }

            exporter = createExporter(server, provider);
            exporter.init();
            exporterMap.put(protocolKey, exporter);
            return exporter;
        }

    }

    protected abstract <T> Exporter<T> createExporter(Server server, Provider<T> provider);
}
