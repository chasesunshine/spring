package com.mashibing;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Student implements ApplicationContextAware, BeanFactoryAware {

    private int id;
    private String name;

    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;


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

    // set方法由谁在什么时候进行调用呢？
    // 由容器来帮我们进行调用
    // 给出一个统一的位置来进行调用即可，提供统一的接口，通过接口来进行判断
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
