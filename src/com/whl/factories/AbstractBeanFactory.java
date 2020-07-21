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
        Object sharedInstance = getSingleton(beanName); // 尝试从缓存中获取
        if (sharedInstance == null) {
            markBeanAsCreated(beanName);
            sharedInstance = createSingleton(beanName, clz);
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
            Method writeMethod = null;
            try {
                writeMethod = ReflectUtils.getWriteMethods(clz).get(fieldBeanName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            assert writeMethod != null;
            if (fieldInstance == null) {
                // 若缓存不存在, 先创建实例在注入
                fieldInstance = doGetBean(field.getType());
            }
            doPopulateBean(writeMethod, sharedInstance, fieldInstance);
        }
        // 所有属性注入成功, 更新缓存
        addSingletonObject(transformedBeanName(clz), sharedInstance);
    }

    /**
     * 调用 writeMethod 进行属性注入
     * @param writeMethod
     * @param beanInstance 需要注入属性的bean实例
     * @param fieldInstance 被注入属性的bean实例
     */
    private void doPopulateBean(Method writeMethod, Object beanInstance, Object fieldInstance) {
        try {
            writeMethod.invoke(beanInstance, fieldInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过class获取到相应的beanName
     * @param clz
     * @return
     */
    private String transformedBeanName(Class<?> clz) {
        String beanName = clz.getSimpleName();
        return (char)(beanName.charAt(0) + 32) + beanName.substring(1);
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
