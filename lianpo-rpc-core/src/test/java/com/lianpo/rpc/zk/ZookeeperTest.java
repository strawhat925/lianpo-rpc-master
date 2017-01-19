package com.lianpo.rpc.zk;

import com.lianpo.rpc.registry.ZookeeperRegistry;

import org.apache.zookeeper.ZooKeeper;


/**
 * Created by Administrator on 2017/1/5.
 */
public class ZookeeperTest {

    public static void main(String[] args){
        for(int i = 0; i < 10;i++){
            ZooKeeper zooKeeper = ZookeeperRegistry.getInstance();
            System.out.println(zooKeeper.getState());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
