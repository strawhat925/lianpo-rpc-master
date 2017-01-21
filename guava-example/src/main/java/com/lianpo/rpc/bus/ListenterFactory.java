package com.lianpo.rpc.bus;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 * 事件监听器静态工厂
 */
public class ListenterFactory {

    private final static class SingletonHolder{
        public static ListenterFactory instance = new ListenterFactory();
    }

    public static ListenterFactory getInstance(){
        return SingletonHolder.instance;
    }

    public EventListener getChangeEventListenter(final EventHandler eventHandler){
        return new ChangeEventListenter(eventHandler);
    }
}
