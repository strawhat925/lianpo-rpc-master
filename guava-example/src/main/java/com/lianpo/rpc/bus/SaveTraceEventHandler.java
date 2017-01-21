package com.lianpo.rpc.bus;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 * 保存流水具体事件处理器
 */
public class SaveTraceEventHandler implements EventHandler {

    @Override
    public void handle(Event event) {
        System.out.println(event);
    }
}
