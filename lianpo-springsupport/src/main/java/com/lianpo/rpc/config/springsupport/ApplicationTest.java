package com.lianpo.rpc.config.springsupport;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lianpo on 2017/1/9.
 *
 * @auther lianpo
 */
public class ApplicationTest {

    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:application.xml");
        //ServerBean serverBean = (ServerBean) context.getBean("server");

        //System.out.println(serverBean);
    }
}
