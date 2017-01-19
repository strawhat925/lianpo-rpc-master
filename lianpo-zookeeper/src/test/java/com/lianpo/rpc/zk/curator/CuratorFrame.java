package com.lianpo.rpc.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;


/**
 * Created by liz on 2017/1/13.
 *
 * @auther liz
 */
@Getter
public class CuratorFrame {

    private CuratorFramework client;

    public void init() {
        CuratorFrameworkFactory.Builder Builder = CuratorFrameworkFactory.builder()
                .connectString("192.168.20.34:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3, 3000))
                //.namespace("")
                .sessionTimeoutMs(3000)
                .connectionTimeoutMs(3000);

        client = Builder.build();
        /*client.start();
        try {
            if (!client.blockUntilConnected(1000 * 3, TimeUnit.MILLISECONDS)) {
                client.close();
                throw new KeeperException.OperationTimeoutException();
            }
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            ex.printStackTrace();
        }*/

    }



    //1、Recipes模块：Elections(选举)，Locks（锁），Barriers（关卡），Atomic（原子量），Caches，Queues等

    /**
     * 本类基于leaderSelector实现,所有存活的client会公平的轮流做leader
     * 如果不想频繁的变化Leader，需要在takeLeadership方法里阻塞leader的变更！ 或者使用 {@link}
     * LeaderLatchClient
     */
    class LeaderSelectorClient extends LeaderSelectorListenerAdapter implements Closeable {
        private final String name;
        private final LeaderSelector leaderSelector;
        private final String PATH = "/motan";
        private final AtomicInteger leaderCount = new AtomicInteger(1);

        public LeaderSelectorClient(CuratorFramework client, String name) {
            this.name = name;
            this.leaderSelector = new LeaderSelector(client, PATH, this);
            //设置一下随机ID
            this.leaderSelector.setId(UUID.randomUUID().toString());
            //保证此实例在释放领导权后还可能获得领导权
            this.leaderSelector.autoRequeue();
        }

        @Override
        public void close() throws IOException {
            this.leaderSelector.close();
        }

        public void start() throws IOException {
            this.leaderSelector.start();
        }

        @Override
        public void takeLeadership(CuratorFramework client) throws Exception {
            while (true){
                int waitSeconds = (int) (5 * Math.random() + 1);
                System.out.println(name + "：是当前客户端leader，选举次数：" + leaderCount.getAndIncrement() + "，选举ID：" + this.leaderSelector.getId());

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println(name + ":让出领导权，选举ID：" + this.leaderSelector.getId());
                }
            }

        }

    }


    //2、locks



    public static void main(String[] args) {

        List<CuratorFramework> clients = new ArrayList<CuratorFramework>();
        List<LeaderSelectorClient> selectorClients = new ArrayList<LeaderSelectorClient>();
        try {
            for (int i = 0; i < 10; i++) {
                CuratorFrame curatorFrame = new CuratorFrame();
                curatorFrame.init();
                clients.add(curatorFrame.client);
                CuratorFrame.LeaderSelectorClient leaderSelectorClient = curatorFrame.new LeaderSelectorClient(curatorFrame.client, "client#" + i);
                selectorClients.add(leaderSelectorClient);

                curatorFrame.client.start();
                leaderSelectorClient.start();

                //Stat stat = curatorFrame.client.checkExists().forPath("/motan");
                //System.out.println("----------------------------stat--" + stat);
            }

            System.out.println("----------先观察一会选举的结果-----------");
            Thread.sleep(10000);

            System.out.println("----------关闭前5个客户端，再观察选举的结果-----------");
            for (int i = 0; i < 5; i++) {
                clients.get(i).close();
            }
            System.out.println("--------------------------------------------------");
            // 这里有个小技巧，让main程序一直监听控制台输入，异步的代码就可以一直在执行。不同于while(ture)的是，按回车或esc可退出
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("===================finally=======================");
            for (LeaderSelectorClient selectorClient : selectorClients) {
                CloseableUtils.closeQuietly(selectorClient);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }

    }

}
