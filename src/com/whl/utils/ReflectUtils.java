package com.whl.utils;

import com.whl.test.ClassA;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author whl1998
 * @Version 1.0
 * @Description
 */
public class ReflectUtils {
    /**
     * 获取到指定 class 所有属性的写方法
     * @param clz class 对象
     * @return key=属性的名称, value=该属性对应的writeMethod
     */
    public static Map<String, Method> getWriteMethods(Class<?> clz) throws ClassNotFoundException {
        Map<String, Method> methods = new HashMap<>();
        Map<Class<?>, String> methodNames = getWriteMethodNames(clz);

        for (Map.Entry<Class<?>, String> entry : methodNames.entrySet()) {
            try {
                methods.put(entry.getKey().getSimpleName(),
                        clz.getDeclaredMethod(entry.getValue(), entry.getKey()));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return methods;
    }

    /**
     * 获取到指定 class 所有属性的写方法名称
     * @param clz 指定 class 的类型
     * @return key=beanType, value=bean对应的writeMethodName
     */
    private static Map<Class<?>, String> getWriteMethodNames(Class<?> clz) {
        assert clz != null;
        Map<Class<?>, String> entry = new HashMap<>();
        for (Field f : clz.getDeclaredFields()) {
            String beanName = f.getType().getSimpleName();
            entry.put(f.getType(), "set" + beanName);
        }
        return entry;
    }

    /**
     * 获取到 class 的无参构造器
     * @param clz class 实例
     * @return 无参构造器
     */
    public static Constructor<?> getNoArgsConstructor(Class<?> clz) {
        if (clz != null) {
            try {
                Constructor<?> constructor = clz.getConstructor();
                constructor.setAccessible(true);
                return constructor;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
