package com.lianpo.rpc.demo;

import com.lianpo.rpc.annotation.RpcServer;

import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/1/5.
 */
@RpcServer(DemoService.class)
@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String sysHello(String name) {
        return "hello " + name;
    }
}
