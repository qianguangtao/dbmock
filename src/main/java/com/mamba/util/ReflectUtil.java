package com.mamba.util;

import com.mamba.mock.mockcolumn.AbstractMockColumn;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 22:53
 * @description: 反射工具类
 */
public class ReflectUtil {
    /**
     * 根据父类扫描指定包下的所有子类，并返回这些子类的Class集合。
     * @param packageName 要扫描的包名
     * @param clazz       父类Class对象
     * @param <T>         父类的类型
     * @return 包含所有子类Class对象的Set集合
     */
    public static <T> Set<Class<? extends T>> scanClassBySuper(String packageName, Class<T> clazz) {
        // 创建 ConfigurationBuilder 实例并指定扫描规则
        ConfigurationBuilder configBuilder =
                new ConfigurationBuilder().forPackages(packageName).filterInputsBy(input -> input.endsWith(".class"));
        // 创建 Reflections 实例
        Reflections reflections = new Reflections(configBuilder);
        // 获取指定包下所有 Runnable 的实现类
        Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(clazz);
        return subTypesOf;
    }

    public static void main(String[] args) {
        Set<Class<? extends AbstractMockColumn>> classes = scanClassBySuper("com.mamba", AbstractMockColumn.class);
        classes.forEach(System.out::println);
    }
}
