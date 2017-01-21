package com.lianpo.rpc.bus;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 * 事件处理器静态工厂
 */
public class EventHandlerFactory {

    private final static class SingletonHolder{
        public static EventHandlerFactory instance = new EventHandlerFactory();
    }

    public static EventHandlerFactory getInstance(){
        return SingletonHolder.instance;
    }


    public EventHandler getSaveTraceEventHandler(){
        return new SaveTraceEventHandler();
    }
}
