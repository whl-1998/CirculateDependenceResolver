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
     * 从缓存池中获取 bean, 若不存在返回null
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
     * 创建一个完整的 Bean 实例, 并添加到缓存
     * @param beanName
     * @param clz
     * @return
     */
    protected Object getSingleton(String beanName, Class<?> clz) {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                boolean newSingleton = false;
                try {
                    singletonObject = ReflectUtils.getNoArgsConstructor(clz).newInstance();
                    newSingleton = true;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (newSingleton) {
                    addSingleton(beanName, singletonObject);
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
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.earlySingletonObjects) {
            this.earlySingletonObjects.put(beanName, singletonObject);
            this.registeredSingletons.add(beanName);
        }
    }
}
