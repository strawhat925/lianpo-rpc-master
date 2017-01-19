package com.lianpo.rpc.config.springsupport;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by lianpo on 2017/1/9.
 *
 * @auther lianpo
 */
public class LianpoNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("service", new LianpoBeanDefinitionParser(ServerBean.class, true));
    }
}
