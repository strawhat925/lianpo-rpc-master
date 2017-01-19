package com.lianpo.rpc.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * zk连接watcher，每次重连创建个新的watcher
 *
 * @auther lianpo
 * @see <a href="http://www.ibm.com/developerworks/cn/opensource/os-cn-apache-zookeeper-watcher/ />
 * </p>
 *
 * Created by lianpo on 2017/1/7.
 */
public class ConnectionWatcher implements Watcher {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionWatcher.class);
    protected ZooKeeper zk;
    //10秒会话时间 ，避免频繁的session expired
    private static final int SESSION_TIME = 10000;
    //连接超时3秒
    private static final int CONNECT_TIME = 3000;
    //初始化一个闭锁
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    protected static String internalHost = "";


    /**
     * 连接
     * @param hosts
     * @throws IOException
     * @throws InterruptedException
     */
    protected void connect(String hosts) throws IOException, InterruptedException {
        this.internalHost = hosts;
        //this -> watcher
        zk = new ZooKeeper(hosts, SESSION_TIME, this);
        // 等待3秒连接建立
        countDownLatch.await(CONNECT_TIME, TimeUnit.MILLISECONDS);

        logger.debug(String.format("zookeeper hosts : %s , connected.", hosts));
    }

    /**
     * 重连
     */
    private void reconnect() {
        logger.debug("start reconnect zookeeper...");

        int retries = 0;
        while (true) {
            if (!zk.getState().equals(ZooKeeper.States.CLOSED)) {
                break;
            }
            //
            try {
                //关闭zk连接
                close();
                //重新连接zk
                connect(internalHost);
                retries++;
            } catch (Exception e) {
                logger.error(String.format("%s reconnect error : %s", retries, e.getMessage()));
                try {
                    //sleep 2 seconds
                    TimeUnit.SECONDS.sleep(ZooKeeperNodeHandle.RETRY_PERIOD_SECONDS);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage(), e);
                }
            }

            //超过最大重试次数
           /* if (retries > ZooKeeperNodeHandle.MAX_RETRIES) {
                logger.error("zookeeper reconnect More than the maximum number of times : " + retries);
                break;
            }*/
        }
    }


    /**
     * 关闭zk连接
     */
    protected void close() throws InterruptedException {
        zk.close();
    }

    /**
     * 客户端会话超时，尝试重连
     * @param watchedEvent
     */
    protected void retry(WatchedEvent watchedEvent) {

        if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            //客户端与服务端处于连接状态
            //
            countDownLatch.countDown();
        } else if (Watcher.Event.KeeperState.Disconnected == watchedEvent.getState()) {
            //客户端与服务端处于断开连接状态
            logger.debug("zookeeper Disconnected.");
        } else if (Watcher.Event.KeeperState.Expired == watchedEvent.getState()) {
            //客户端会话超时，尝试重连
            reconnect();
        } else {
            //do something
        }
    }

    /**
     * watcher相当于callback
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        //重连
        retry(watchedEvent);
    }
}
