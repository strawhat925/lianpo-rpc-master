package com.lianpo.rpc.zk;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * <p>
 *   zk服务类
 *   单例模式保证类启动时zk只被初始化一次
 * </p>
 * Created by lianpo on 2017/1/7.
 *
 * @auther lianpo
 */
public class ZookeeperRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private ZooKeeperNodeHandle zooKeeperNodeHandle;

    private ZookeeperRegistry() {

    }

    private static class SignletonHolder {
        public static ZookeeperRegistry instance = new ZookeeperRegistry();
    }

    public static ZookeeperRegistry getInstance() {
        return SignletonHolder.instance;
    }

    /**
     * 初始化
     * @description 初始化zookeeper服务地址，如hosts="192.168.131.4:2181,192.168.131.3:2181"
     * @param hosts
     */
    public ZooKeeperNodeHandle init(String hosts) {
        try {
            zooKeeperNodeHandle = new ZooKeeperNodeHandle();
            zooKeeperNodeHandle.connect(hosts);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return zooKeeperNodeHandle;
    }




    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZookeeperRegistry zk = ZookeeperRegistry.getInstance();
        ZooKeeperNodeHandle zooKeeperNodeHandle = zk.init("192.168.20.34:2181");
        List<String> list = zooKeeperNodeHandle.getChildren("/");
        System.out.println(list.toString());

        try {
            Stat stat = new Stat();
            String str = zooKeeperNodeHandle.getData("/motan/alibaba_1/com.pyw.service.authorizer.api.AuthorizerService/server/192.168.20.51:8002",stat);
            System.out.println(str);
            if(!zooKeeperNodeHandle.exists("/rpc")){
                //System.out.println("==================");
                //zooKeeperNodeHandle.create("/rpc", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //zooKeeperNodeHandle.appendEphemeralNode("/rpc/rpc", "ssdfsdf");
            //zooKeeperNodeHandle.deleleNode("/rpc", -1);
            zooKeeperNodeHandle.appendEphemeralNode("/rpc","sss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
