package com.whl;

import com.whl.factories.DefaultListableBeanFactory;
import com.whl.test.ClassA;

public class Main {
    public static void main(String[] args) {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        ClassA a = (ClassA) factory.getBean(ClassA.class);
    }
}
