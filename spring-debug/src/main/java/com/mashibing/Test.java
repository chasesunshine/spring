package com.mashibing;

import com.mashibing.config.MyPropertySource;
import com.mashibing.selfConverter.Student;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.convert.ConversionService;

public class Test {

    public static void main(String[] args) {
        MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("applicationContext.xml");
//        System.out.println(ac.getBean(MyPropertySource.class).getName());
//        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-${username}.xml");
//        Person bean = ac.getBean(Person.class);
//        System.out.println(bean);
//        A bean1 = ac.getBean(A.class);
//        System.out.println(bean1);
//        ac.close();
//        User user = (User) ac.getBean("msb");
//        System.out.println(user.getUsername());

//        MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("selfEditor.xml");
//        Customer bean = ac.getBean(Customer.class);
//        System.out.println(bean);
//        ConversionService bean = ac.getBean(ConversionService.class);
//        Student convert = bean.convert("1_zhangsan", Student.class);
//        System.out.println(convert);
    }
}
