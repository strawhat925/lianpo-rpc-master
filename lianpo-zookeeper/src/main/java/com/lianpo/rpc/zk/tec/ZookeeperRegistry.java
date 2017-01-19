package com.lianpo.rpc.zk.tec;


import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;

/**
 * Created by lianpo on 2017/1/12.
 *
 * @auther lianpo
 */
public class ZookeeperRegistry{
    private ZkClient zkClient;

    public ZookeeperRegistry(String zkServers, ZkClient zkClient) {
        this.zkClient = zkClient;
        IZkStateListener iZkStateListener = new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

            }

            @Override
            public void handleNewSession() throws Exception {

            }
        };
        zkClient.subscribeStateChanges(iZkStateListener);
    }



    public void doRegistry(String path){
    }



}
