package com.lianpo.rpc.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * <p>
 *     节点变更watcher
 *     用来通知客户端节点变更
 * </p>
 * Created by lianpo on 2017/1/7.
 *
 * @auther lianpo
 */
public class NodeWatcher implements Watcher {

    public NodeWatcher(){

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        //do something
        //callback
    }
}
