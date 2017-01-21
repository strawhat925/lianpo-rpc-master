package com.lianpo.rpc.bus;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 */
public class BusTest {

    public static void main(String[] args){
        EventHandler eventHandler = EventHandlerFactory.getInstance().getSaveTraceEventHandler();
        EventListener eventListener = ListenterFactory.getInstance().getChangeEventListenter(eventHandler);
        ExecEventBus execEventBus = new ExecEventBus(eventListener);

        System.out.println(eventListener.getIdentity());
        TraceStatusEvent traceStatusEvent = new TraceStatusEvent("123456", "john");
        execEventBus.post(traceStatusEvent);
    }
}
