package com.lianpo.rpc.helloword;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by liz on 2017/1/20.
 *
 * @auther liz
 * /**
 * 在设计模式中对Builder模式的定义是用于构建复杂对象的一种模式，
 * 所构建的对象往往需要多步初始化或赋值才能完成。
 * 那么，在实际的开发过程中，我们哪些地方适合用到Builder模式呢？
 * 其中使用Builder模式来替代多参数构造函数是一个比较好的实践法则
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class JobProperties {

    private final String id;
    private final String name;
    private final String cron;
    private final long lastTime;
    private final String status;

    public static Builder newBuilder(final String id, final String name, final String cron) {
        return new Builder(id, name, cron);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private final String id;
        private final String name;
        private final String cron;
        private long lastTime;
        private String status;

        public Builder lastTime(final long lastTime) {
            this.lastTime = lastTime;
            return this;
        }

        public Builder status(final String status) {
            this.status = status;
            return this;
        }


        public final JobProperties bulid() {
            return new JobProperties(id, name, cron, lastTime, status);
        }

    }

    @Override
    public String toString() {
        return "JobProperties{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cron='" + cron + '\'' +
                ", lastTime=" + lastTime +
                ", status='" + status + '\'' +
                '}';
    }

    public static void main(String[] args){
        JobProperties jobProperties = JobProperties.newBuilder("1","job","1 1 ").status("yew").bulid();
        System.out.println(jobProperties);
    }

}
