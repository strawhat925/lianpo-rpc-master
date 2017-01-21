package com.lianpo.rpc.bus;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by liz on 2017/1/21.
 *
 * @auther liz
 *
 * 流水配置事件信息
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class TraceStatusEvent implements Event {

    private final String id;
    private final String name;

    private Date date = new Date();

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public String toString() {
        return "TraceStatusEvent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
