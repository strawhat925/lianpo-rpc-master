package com.lianpo.rpc.bus;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 * 流水id
 */
public class TraceEventIdentity implements EventIdentity {
    @Override
    public String getIdentity() {
        return "trace";
    }
}
