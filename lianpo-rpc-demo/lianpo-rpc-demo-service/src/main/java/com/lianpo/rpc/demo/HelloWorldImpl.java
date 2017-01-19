package com.lianpo.rpc.demo;

import com.lianpo.rpc.annotation.RpcServer;

import org.springframework.stereotype.Service;

/**
 * Created by lianpo on 2017/1/10.
 *
 * @auther lianpo
 */
@RpcServer(HelloWorld.class)
@Service
public class HelloWorldImpl implements HelloWorld {

    @Override
    public String sysHello(String name) {
        return "my name is " + name;
    }
}
