package com.mashibing;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Address implements BeanFactoryAware, ApplicationContextAware {

    private String city;
    private String privince;

    private BeanFactory beanFactory;
    private ApplicationContext applicationContext;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrivince() {
        return privince;
    }

    public void setPrivince(String privince) {
        this.privince = privince;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    // 由谁来进行调用，是用户自己调用嘛？
    // 容器来负责整个对象的创建销毁过程，自己怎么去调用？应该交给容器来控制
    // 为了方便管理，提供了一个统一的方式来调用这些set方法
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
