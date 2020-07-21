package com.whl.test;

/**
 * @Author whl1998
 * @Version 1.0
 * @Description
 */
public class ClassA {
    private ClassB classB;

    public ClassA() {
    }

    public ClassA(ClassB classB) {
        this.classB = classB;
    }

    public ClassB getClassB() {
        return classB;
    }

    public void setClassB(ClassB classB) {
        this.classB = classB;
    }
}
