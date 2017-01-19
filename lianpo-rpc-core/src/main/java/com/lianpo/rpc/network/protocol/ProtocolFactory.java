package com.lianpo.rpc.network.protocol;

/**
 * Created by lianpo on 2017/1/9.
 *
 * @auther lianpo
 */
public class ProtocolFactory {

    public static Protocol getDefaultProtocol(String name){
        return DefaultRpcProtocol.getInstance();
    }
}
