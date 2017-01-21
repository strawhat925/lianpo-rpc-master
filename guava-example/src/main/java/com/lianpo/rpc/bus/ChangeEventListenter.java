package com.lianpo.rpc.bus;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 *
 * 具体事件监听器
 */
public class ChangeEventListenter extends TraceEventIdentity implements EventListener {

    private final EventHandler eventHandler;

    public ChangeEventListenter(final EventHandler eventHandler){
        this.eventHandler = eventHandler;
    }

    @Override
    public void listen(Event event) {
        this.eventHandler.handle(event);
    }
}
