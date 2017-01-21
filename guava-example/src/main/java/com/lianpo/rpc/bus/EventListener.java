package com.lianpo.rpc.bus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 * 事件监听器
 */
public interface EventListener extends EventIdentity{

    //订阅事件
    @Subscribe
    //并发设置
    @AllowConcurrentEvents
    void listen(Event event);
}
