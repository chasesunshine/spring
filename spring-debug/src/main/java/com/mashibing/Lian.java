package com.mashibing;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Lian implements ApplicationContextAware, BeanFactoryAware {

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

    // 什么时候调用当前方法，有谁来调用当前方法？是用户自己操作嘛？
    // 对象的创建都交给容器来处理了，那么对象的属性赋值工作也应该由容器帮我们进行处理吧
    // 定义一个接口，在统一的地方对其进行调用处理
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
