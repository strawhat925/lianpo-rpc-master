package com.lianpo.rpc.zk.curator;

import org.apache.curator.framework.recipes.locks.InterProcessLock;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by liz on 2017/1/13.
 * 互斥任务
 *
 * @auther liz
 */
public class MutexJob implements Runnable {

    private final String name;
    private final InterProcessLock lock;
    private final int waitTime = 10;

    public MutexJob(String name, InterProcessLock lock) {
        this.name = name;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            dowork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dowork() throws Exception {

        try {
            System.out.println("MutexJob--------------------------------------------------" + lock);
            if (!lock.acquire(waitTime, TimeUnit.SECONDS)) {
                System.err.println(name + "等待" + waitTime + "秒，仍未能获取到lock,准备放弃。");
            }

            // 模拟job执行时间0-2000毫秒
            int exeTime = new Random().nextInt(2000);
            System.out.println(name + "开始执行,预计执行时间= " + exeTime + "毫秒----------");
            Thread.sleep(exeTime);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.release();
        }
    }
}
