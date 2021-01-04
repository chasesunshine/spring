package com.mashibing.cycle;

import org.springframework.beans.factory.annotation.Autowired;


public class A {

//    @Autowired
    private B b;

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "A{" +
                "b=" + b +
                '}';
    }
}
