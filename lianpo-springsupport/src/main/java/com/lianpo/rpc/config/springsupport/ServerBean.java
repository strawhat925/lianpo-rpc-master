package com.lianpo.rpc.config.springsupport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by linapo on 2017/1/9.
 *
 * @auther linapo
 */
public class ServerBean implements BeanPostProcessor,
        BeanFactoryAware,
        InitializingBean,
        DisposableBean,
        ApplicationListener<ContextRefreshedEvent> {

    private String id;

    private String name;

    private String ref;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }


    @Override
    public String toString() {
        return "ServerBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("===================================" + this);
        System.out.println("===================================" + beanFactory);
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("===================================destroy" );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("===================================" + this);
        System.out.println("===================================afterPropertiesSet" );
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        System.out.println("===================================" + this);
        System.out.println("===================================postProcessBeforeInitialization" );
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        System.out.println("===================================" + this);
        System.out.println("===================================postProcessAfterInitialization" );
        return null;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("===================================" + this);
        System.out.println("===================================onApplicationEvent" );
    }
}
