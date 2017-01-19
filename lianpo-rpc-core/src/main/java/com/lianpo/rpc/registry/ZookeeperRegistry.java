package com.lianpo.rpc.registry;


import com.lianpo.rpc.util.Environment;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * zookeeper
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public class ZookeeperRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private static ZooKeeper zooKeeper;
    private static ReentrantLock INSTANCE_INIT_LOCK = new ReentrantLock();

    //==============================================zookeeper instance==========================
    public static ZooKeeper getInstance() {
        if (zooKeeper == null) {
            try {
                //
                if (INSTANCE_INIT_LOCK.tryLock(2, TimeUnit.SECONDS)) {
                    try {
                        zooKeeper = new ZooKeeper(Environment.ZK_ADDRESS, 30000, new Watcher() {
                            @Override
                            public void process(WatchedEvent watchedEvent) {
                                if (watchedEvent.getState() == Event.KeeperState.Expired) {
                                    try {
                                        zooKeeper.close();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                                //zookeeper的watcher是一次性的，用过了需要再注册
                                try {
                                    String zooPath = watchedEvent.getPath();
                                    if (zooPath != null) {
                                        zooKeeper.exists(zooPath, true);
                                    }
                                } catch (InterruptedException e) {
                                    logger.error(e.getMessage(), e);
                                } catch (KeeperException e) {
                                    logger.error(e.getMessage(), e);
                                }

                            }
                        });

                        logger.info("lianpo-rpc registry zookeeper success");
                    } finally {
                        INSTANCE_INIT_LOCK.unlock();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return zooKeeper;
    }


    /**
     * register service
     */
    public static void registerService(int port, Set<String> serviceList) throws KeeperException, InterruptedException {
        if (port < 0 || serviceList == null || serviceList.size() == 0) {
            return;
        }

        String ip = Environment.getLocalhostAddress();
        if (ip == null) {
            return;
        }

        StringBuffer serverAddress = new StringBuffer();
        serverAddress.append(ip);
        serverAddress.append(":");
        serverAddress.append(port);

        Stat stat = zooKeeper.exists(Environment.ZK_SERVICE_PATH, true);
        if (stat == null) {
            //ACL @see <a href="http://www.tuicool.com/articles/77FJzuj"/>
            //1、OPEN_ACL_UNSAFE ：完全开放。 事实上这里是采用了world验证模式，由于每个zk连接都有world验证模式，所以znode在设置了 OPEN_ACL_UNSAFE 时，是对所有的连接开放。
            //2、CREATOR_ALL_ACL ：给创建该znode连接所有权限。 事实上这里是采用了auth验证模式，使用sessionID做验证。所以设置了 CREATOR_ALL_ACL 时，创建该znode的连接可以对该znode做任何修改。
            //3、READ_ACL_UNSAFE ：所有的客户端都可读。 事实上这里是采用了world验证模式，由于每个zk连接都有world验证模式，所以znode在设置了READ_ACL_UNSAFE时，所有的连接都可以读该znode。
            getInstance().create(Environment.ZK_SERVICE_PATH, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        for (String interfaceName : serviceList) {
            String interfacePath = Environment.ZK_SERVICE_PATH.concat("/").concat(interfaceName);
            String addressPath = Environment.ZK_SERVICE_PATH.concat("/").concat(interfaceName).concat("/").concat(serverAddress.toString());

            Stat istat = getInstance().exists(interfacePath, true);
            if (istat == null) {
                getInstance().create(interfacePath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            Stat astat = getInstance().exists(addressPath, true);
            if (stat != null) {
                getInstance().delete(addressPath, -1);
            }
            getInstance().create(addressPath, serverAddress.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }

    }


    public static void destroy() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                logger.info("zookeeper destroy success", e.getMessage());
            }
        }
    }
}
