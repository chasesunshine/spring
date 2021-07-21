package com.mashibing;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class Dog implements ApplicationContextAware, BeanFactoryAware, EnvironmentAware {

    private int id;
    private String name;

    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private Environment environment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 此方法必须要进行调用，此时才能给属性赋值，什么时候谁来调用此方法？必须由容器来进行调用，
    // 能否让这些set方法在某一个固定的时候进行调用？定义统一的接口规范，在指定的步骤完成此功能
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
