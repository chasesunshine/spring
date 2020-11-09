package com.mashibing;

import com.mashibing.methodOverrides.lookup.FruitPlate;
import com.mashibing.methodOverrides.replace.OriginalDog;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMethodOverrides {

    public static void main(String[] args) {
        ApplicationContext app = new ClassPathXmlApplicationContext("bean.xml");

//        FruitPlate fp1= (FruitPlate)app.getBean("fruitPlate1");
//        FruitPlate fp2 = (FruitPlate)app.getBean("fruitPlate2");
//
//        fp1.getFruit();
//        fp2.getFruit();
        OriginalDog originalDogReplaceMethod = app.getBean("originalDogReplaceMethod", OriginalDog.class);
        originalDogReplaceMethod.sayHello("结果被 替换");
    }
}
