package com.lianpo.rpc.leader;

import com.google.common.collect.Lists;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by liz on 2017/1/17.
 *
 * @auther liz
 */
public class LeaderLatchExample {


    public static void main(String[] args) {
        List<LeaderLatch> leaders = Lists.newArrayList();
        List<CuratorFramework> clients = Lists.newArrayList();

        try {
            for (int i = 0; i < 10; i++) {
                CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.20.34:2181,192.168.20.33:2181", new ExponentialBackoffRetry(1000, 3));
                clients.add(client);

                final LeaderLatch leaderLatch = new LeaderLatch(client, "/motan", "client-" + i);
                leaderLatch.addListener(new LeaderLatchListener() {
                    @Override
                    public void isLeader() {
                        System.out.println("I am Leader:" + leaderLatch.getId());
                    }

                    @Override
                    public void notLeader() {
                        System.out.println("I am not Leader");
                    }
                });
                leaders.add(leaderLatch);

                client.start();
                leaderLatch.start();

            }
            //等待选举结果
            Thread.sleep(10000);
            for (LeaderLatch leaderLatch : leaders) {
                //判断是否master
                if (leaderLatch.hasLeadership()) {
                    System.out.println("===================" + leaderLatch.getLeader().getId());
                }
            }

            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {

        } finally {
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
            for (LeaderLatch leaderLatch : leaders) {
                CloseableUtils.closeQuietly(leaderLatch);
            }
        }

    }

    private static void printLeader(List<LeaderLatch> leaders){
        for (LeaderLatch leaderLatch : leaders) {
            if (leaderLatch.hasLeadership()) {
                System.out.println("===================" + leaderLatch.getId());
            }
        }
    }
}
