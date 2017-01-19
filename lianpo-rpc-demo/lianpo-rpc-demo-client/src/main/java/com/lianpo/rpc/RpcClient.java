package com.lianpo.rpc;

import com.lianpo.rpc.demo.DemoService;
import com.lianpo.rpc.demo.HelloWorld;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/1/5.
 */
public class RpcClient {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:rpc-consumer.xml");
        //DemoService demoService = (DemoService) context.getBean("demoService");
        //System.out.println(demoService.sysHello("稍等发送到发送到粉色发"));
        /*final CyclicBarrier cyclicBarrier = new CyclicBarrier(100);
        Executor executor = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        long start = System.currentTimeMillis();
        for(int i = 0;i < 100;i++){
            executor.execute(new TestThread(cyclicBarrier,demoService,countDownLatch,i));
        }
        long end = System.currentTimeMillis();
        countDownLatch.await();
        System.out.println("执行消耗时间：" + (end - start));*/
        //HelloWorld helloWorld = (HelloWorld) context.getBean("helloWorld");
        //System.out.println(helloWorld.sysHello("lianpo"));
    }


    static class TestThread implements Runnable{
        private CyclicBarrier cyclicBarrier;
        private DemoService demoService;
        private CountDownLatch countDownLatch;
        private int index;
        public TestThread(CyclicBarrier cyclicBarrier,DemoService demoService, CountDownLatch countDownLatch,int index){
            this.cyclicBarrier = cyclicBarrier;
            this.demoService = demoService;
            this.countDownLatch = countDownLatch;
            this.index = index;
        }
        @Override
        public void run() {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(demoService.sysHello("稍等发送到发送到粉色发" + index));
            countDownLatch.countDown();
        }
    }
}
