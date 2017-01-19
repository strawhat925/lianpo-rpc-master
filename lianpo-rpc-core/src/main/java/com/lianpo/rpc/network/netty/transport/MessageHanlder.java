package com.lianpo.rpc.network.netty.transport;

import com.lianpo.rpc.network.netty.server.Channel;

/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public interface MessageHanlder {

    Object handle(Channel channel, Object message);
}
