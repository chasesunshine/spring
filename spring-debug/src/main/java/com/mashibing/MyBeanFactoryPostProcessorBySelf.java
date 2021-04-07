package com.mashibing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class MyBeanFactoryPostProcessorBySelf implements BeanFactoryPostProcessor{


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        BeanDefinition person = beanFactory.getBeanDefinition("person");
//        person.set
        System.out.println("2021年3月29日");
    }
}
