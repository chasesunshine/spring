package com.mashibing;

import javafx.application.Application;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        Person bean = ac.getBean(Person.class);
        System.out.println(bean);
//        A bean1 = ac.getBean(A.class);
//        System.out.println(bean1);
    }
}
