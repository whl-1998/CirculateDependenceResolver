package com.whl.factories;

import com.whl.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author whl1998
 * @Version 1.0
 * @Description
 */
public class DefaultSingletonBeanRegistry {

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    /**
     * 从缓存池中获取 bean
     * 先去 singletonObjects 里尝试获取
     * 若获取不到, 再去 earlySingletonObjects 里尝试获取
     * @param beanName
     * @return
     */
    protected Object getSingleton(String beanName) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
            }
        }
        return singletonObject;
    }

    /**
     * 通过无参构造器创建一个 bean 实例
     * @param beanName
     * @param clz
     * @return
     */
    protected Object createSingleton(String beanName, Class<?> clz) {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                boolean newSingleton = false;
                try {
                    singletonObject = clz.newInstance();
                    newSingleton = true;
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (newSingleton) {
                    // 放入二级缓存
                    addEarlySingleton(beanName, singletonObject);
                }
            }
            return singletonObject;
        }
    }

    /**
     * 添加到二级缓存
     * @param beanName
     * @param singletonObject
     */
    protected void addEarlySingleton(String beanName, Object singletonObject) {
        synchronized (this.earlySingletonObjects) {
            this.earlySingletonObjects.put(beanName, singletonObject);
            this.registeredSingletons.add(beanName);
        }
    }

    /**
     * 更新缓存
     * @param beanName
     * @param singletonObject
     */
    protected void addSingletonObject(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.earlySingletonObjects.remove(beanName);
            this.singletonObjects.put(beanName, singletonObject);
        }
    }
}
