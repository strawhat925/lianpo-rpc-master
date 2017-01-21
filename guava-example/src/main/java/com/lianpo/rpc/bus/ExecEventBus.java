package com.lianpo.rpc.bus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import com.dangdang.ddframe.job.util.concurrent.ExecutorServiceObject;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 * 注册事件监听、订阅消费
 */
public class ExecEventBus {

    private final EventBus eventBus;
    private final ExecutorServiceObject executorServiceObject;
    private final EventListener eventListener;

    public ExecEventBus(){
        eventBus = null;
        executorServiceObject = null;
        eventListener = null;
    }

    public ExecEventBus(final EventListener eventListener){
        this.eventListener = eventListener;
        executorServiceObject = new ExecutorServiceObject("event-thread", Runtime.getRuntime().availableProcessors() * 2);
        //异步线程消费
        eventBus = new AsyncEventBus(executorServiceObject.createExecutorService());
        //注册监听
        register();
    }


    protected void register(){
        this.eventBus.register(this.eventListener);
    }

    public void post(Event event){
        this.eventBus.post(event);
    }
}
