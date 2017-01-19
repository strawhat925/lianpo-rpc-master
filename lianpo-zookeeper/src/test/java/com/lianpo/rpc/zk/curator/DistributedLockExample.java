package com.lianpo.rpc.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.utils.CloseableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liz on 2017/1/13.
 * 分布式锁实例
 * @auther liz
 */
public class DistributedLockExample {

    private final CuratorFramework client;

    // 进程内部（可重入）读写锁
    private final InterProcessReadWriteLock lock;
    // 读锁
    private final InterProcessLock readLock;
    // 写锁
    private final InterProcessLock writeLock;
    private final String PATH = "/motan";

    public DistributedLockExample(CuratorFramework client){
        this.client = client;
        this.lock = new InterProcessReadWriteLock(this.client, PATH);
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }



    public static void main(String[] args){
        CuratorFrame curatorFrame = new CuratorFrame();
        curatorFrame.init();
        curatorFrame.getClient().start();
        DistributedLockExample example = new DistributedLockExample(curatorFrame.getClient());

        try{
            List<Thread> jobs = new ArrayList<Thread>();
            for(int i = 0; i < 10; i++){
                jobs.add(new Thread(new ParallelJob("parallenl" + i, example.readLock)));
            }

            for(int i = 0; i < 10; i++){
                jobs.add(new Thread(new MutexJob("mutex" +i, example.writeLock)));
            }

            for(Thread thread : jobs){
                thread.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //CloseableUtils.closeQuietly(curatorFrame.getClient());
        }
    }
}
