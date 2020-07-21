package com.whl.factories;

import com.whl.test.ClassA;

/**
 * @Author whl1998
 * @Version 1.0
 * @Description
 */
public class DefaultListableBeanFactory extends AbstractBeanFactory {
    public Object getBean(Class<?> clz) {
        return doGetBean(clz);
    }
}
