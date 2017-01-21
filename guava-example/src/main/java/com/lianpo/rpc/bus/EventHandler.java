package com.lianpo.rpc.bus;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 * 处理具体事件接口
 */
public interface EventHandler{

    void handle(Event event);
}
