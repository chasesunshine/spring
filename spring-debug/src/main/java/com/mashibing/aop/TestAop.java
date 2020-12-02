package com.mashibing.aop;

import com.mashibing.aop.service.MyCalculator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestAop {

    public static void main(String[] args) throws NoSuchMethodException {
        ApplicationContext ac = new ClassPathXmlApplicationContext("aop.xml");
        MyCalculator bean = ac.getBean(MyCalculator.class);
        bean.add(1,1);
    }
}
