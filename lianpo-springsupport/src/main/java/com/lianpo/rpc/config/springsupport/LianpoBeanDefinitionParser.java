package com.lianpo.rpc.config.springsupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by linpo on 2017/1/9.
 *
 * @auther linpo
 */
public class LianpoBeanDefinitionParser implements BeanDefinitionParser {
    private final Class<?> beanClass;
    private final boolean required;

    public LianpoBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }


    public BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
        RootBeanDefinition bd = new RootBeanDefinition();
        bd.setBeanClass(beanClass);
        bd.setLazyInit(false);
        String id = element.getAttribute("id");

        if ((id == null || id.length() == 0) && required) {
            String generatedBeanName = element.getAttribute("name");
            if (generatedBeanName == null || generatedBeanName.length() == 0) {
                generatedBeanName = element.getAttribute("class");
            }
            if (generatedBeanName == null || generatedBeanName.length() == 0) {
                generatedBeanName = beanClass.getName();
            }
            id = generatedBeanName;
            int counter = 2;
            while (parserContext.getRegistry().containsBeanDefinition(id)) {
                id = generatedBeanName + (counter++);
            }
        }
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id)) {
                throw new IllegalStateException("Duplicate spring bean id " + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id, bd);
        }
        bd.getPropertyValues().addPropertyValue("id", id);

        for (Method setter : beanClass.getMethods()) {
            String methodName = setter.getName();
            //setXXX
            if (methodName.length() <= 3 || !methodName.startsWith("set") || !Modifier.isPublic(setter.getModifiers()) || setter.getParameterTypes().length != 1) {
                continue;
            }

            String property = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
            if ("id".equalsIgnoreCase(property)) {
                bd.getPropertyValues().addPropertyValue("id", id);
                continue;
            }
            String value = element.getAttribute(property);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            value = value.trim();
            if(value.length() == 0){
                continue;
            }
            Object reference = null;
            if ("ref".equalsIgnoreCase(property)) {
                if (parserContext.getRegistry().containsBeanDefinition(property)) {
                    BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
                    if (!refBean.isSingleton()) {
                        throw new IllegalStateException("The service ref " + value + " must be singleton! Please set the " + value
                                + " bean scope to singleton, eg: <bean id=\"" + value + "\" scope=\"singleton\" ...>");
                    }

                    reference = new RuntimeBeanNameReference(value);
                }
            }else{
                reference = new TypedStringValue(value);
            }

            if (reference != null) {
                bd.getPropertyValues().addPropertyValue(property, reference);
            }
        }
        return bd;
    }
}
