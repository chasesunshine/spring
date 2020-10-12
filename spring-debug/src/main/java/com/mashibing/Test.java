package com.mashibing;

import javafx.application.Application;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    public static void main(String[] args) {
        MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("applicationContext.xml");
//        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-${username}.xml");
//        Person bean = ac.getBean(Person.class);
//        System.out.println(bean);
//        A bean1 = ac.getBean(A.class);
//        System.out.println(bean1);
//        ac.close();
    }
}
