package com.mashibing;

import com.mashibing.cycle.A;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCycle {

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("cycle.xml");
    }
}
