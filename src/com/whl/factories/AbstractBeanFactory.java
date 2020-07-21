package com.whl.factories;

import com.whl.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author whl1998
 * @Version 1.0
 * @Description
 */
public class AbstractBeanFactory extends DefaultSingletonBeanRegistry {
    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

    /**
     * 创建 bean 实例, 并注入属性
     * @param clz
     * @return
     */
    protected Object doGetBean(Class<?> clz) {
        final String beanName = transformedBeanName(clz);
        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance == null) {
            markBeanAsCreated(beanName);
            sharedInstance = getSingleton(beanName, clz);

            populateBean(clz, sharedInstance);
        }
        return sharedInstance;
    }

    /**
     * 属性注入
     * @param clz
     * @param sharedInstance
     */
    private void populateBean(Class<?> clz, Object sharedInstance) {
        for (Field field : clz.getDeclaredFields()) {
            String fieldBeanName = transformedBeanName(field.getType());
            Object fieldInstance = getSingleton(fieldBeanName);
            if (fieldInstance != null) {
                try {
                    Method writeMethod = ReflectUtils.getWriteMethods(clz).get(fieldBeanName);
                    writeMethod.setAccessible(true);
                    writeMethod.invoke(sharedInstance, fieldInstance);
                } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                fieldInstance = doGetBean(field.getType());
                Method writeMethod = null;
                try {
                    writeMethod = ReflectUtils.getWriteMethods(clz).get(fieldBeanName);
                    writeMethod.setAccessible(true);
                    writeMethod.invoke(sharedInstance, fieldInstance);
                } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过class获取到相应的beanName
     * @param clz
     * @return
     */
    private String transformedBeanName(Class<?> clz) {
        return clz.getSimpleName();
    }

    /**
     * 标识一个 bean 已经处于创建中
     * @param beanName
     */
    protected void markBeanAsCreated(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            if (!this.alreadyCreated.contains(beanName)) {
                this.alreadyCreated.add(beanName);
            }
        }
    }
}
