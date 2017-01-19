package com.lianpo.rpc.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * zk节点读写操作
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public class ZooKeeperNodeHandle extends ConnectionWatcher {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperNodeHandle.class);
    // 每次重试超时时间
    public static final int RETRY_PERIOD_SECONDS = 2;
    //最大重试次数
    public static final int MAX_RETRIES = 5;
    //字符集
    private static final String CHARSET = "UTF-8";

    /**
     *
     * @param path
     * @description 获取指定节点下的所有子节点
     * @return
     * @throws KeeperException
     */
    public List<String> getChildren(String path) throws KeeperException, InterruptedException{
        List<String> children = new ArrayList<String>();
        try {
            children = zk.getChildren(path, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return children;
    }

    /**
     * 递归获取path所有节点
     * @param path
     * @return
     */
    public List<String> getRecursionChildren(String path) throws Exception {
        List<String> children = new ArrayList<String>();
        //可用链表Node实现节点关系
        if(exists(path)){

        }
        return null;
    }

    /**
     * 判断指定目录节点是否存在
     * @param path
     * @return
     * @throws Exception
     */
    public boolean exists(String path) throws Exception {
        //keep zk
        int retries = 0;
        while (true) {
            try {
                Stat stat = zk.exists(path, false);
                if (stat == null) {
                    return false;
                } else {
                    return true;
                }
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }

    /**
     * 创建
     * @param path
     * @param data
     * @param acl
     * @param createMode 标识有四种形式的目录节点,分别是
     * PERSISTENT：持久化目录节点，这个目录节点存储的数据不会丢失；
     * PERSISTENT_SEQUENTIAL：顺序自动编号的目录节点，这种目录节点会根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名；
     * EPHEMERAL：临时目录节点，一旦创建这个节点的客户端与服务器端口会话失效，这种节点会被自动删除；
     * EPHEMERAL_SEQUENTIAL：临时自动编号节点
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws Exception {
        int retries = 0;
        while (true){
            try {
                zk.create(path, data, acl, createMode);
                break;
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }



     /**
     * @param path
     * @param data
     * @description 创建持久化目录节点,直到有删除操作来主动清除这个节点；
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void appendPresistentNode(String path, String data) throws Exception {
        int retries = 0;
        while (true){
            try {
                zk.create(path, data.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                break;
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }



    /**
     *
     * @param path
     * @param data
     * @description 创建持久化目录顺序节点,直到有删除操作来主动清除这个节点；
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void appendPresistentSequentialNode(String path, String data) throws Exception {
        int retries = 0;
        while (true){
            try {
                zk.create(path, data.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
                break;
            }catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }


    /**
     *
     * @param path
     * @param data
     * @description 创建临时目录节点，一旦创建这个节点的客户端与服务器端口会话失效，这种节点会被自动删除；
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void appendEphemeralNode(String path, String data) throws Exception {
        int retries = 0;
        while (true){
            try {
                zk.create(path, data.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                break;
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }

    /**
     *
     * @param path
     * @param data
     * @description 创建临时顺序目录节点，一旦创建这个节点的客户端与服务器端口会话失效，这种节点会被自动删除；
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void appendEphemeralSequentialNode(String path, String data) throws Exception {
        int retries = 0;
        while (true){
            try {
                zk.create(path, data.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                break;
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }



    /**
     *
     * @param path
     * @description 获取指定节点上的数据
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String getData(String path, Stat stat) throws Exception {
        int retries = 0;
        while (true){
            try {
                byte[] data = zk.getData(path, true, stat);
                return new String(data == null ? new byte[0] : data);
            } catch (Exception e) {
                exception(e, retries);
            }
        }
    }


    /**
     *
     * @param path
     * @param data
     * @description 变更设置指定节点上的数据
     * @throws KeeperException
     */
    public void setData(String path, String data, int version) throws Exception {
        int retries = 0;
        while (true){
            try {
                zk.setData(path, data.getBytes(CHARSET), version);
                break;
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }


    /**
     *
     * @param path
     * @param version -1 如果版本号与节点的版本号不一致，将无法删除，是一种乐观加锁机制；如果将版本号设置为-1，不会去检测版本，直接删除；
     * @description 删除指定目录节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void deleleNode(String path, int version) throws Exception {
        int retries = 0;
        while (true){
            try {
                zk.delete(path, version);
                break;
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            }catch (KeeperException e) {
                exception(e, retries);
            }
        }
    }



    private void exception(Exception e,int retries) throws Exception {
        if(zk == null){
            retries++;
            if(retries == MAX_RETRIES){
                logger.error(String.format("zookeeper retry %s times connect fail.", retries));
                throw e;
            }

            TimeUnit.SECONDS.sleep(RETRY_PERIOD_SECONDS);
        }else{
            throw e;
        }
    }
}


