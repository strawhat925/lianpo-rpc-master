package com.lianpo.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by liz on 2017/1/17.
 *
 * @auther liz
 */
public class Test1 {

    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:elastic-job.xml");

        /*context.start();

        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }*/
    }
}
