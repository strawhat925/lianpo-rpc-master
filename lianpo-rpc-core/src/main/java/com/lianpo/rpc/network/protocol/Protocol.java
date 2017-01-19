package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.network.netty.server.Server;


/**
 * Created by lianpo on 2017/1/9.
 *
 * @auther lianpo
 */
public interface Protocol {

    public <T> Exporter<T> export(Server server, Provider<T> provider);
}

