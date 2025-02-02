package com.mamba.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
import com.mamba.annotations.TableField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/24 9:18
 * @description: hutool db转换pojo工具类
 */
@Slf4j
public class DBConverter {
    /**
     * 将Entity对象转换成指定类型的POJO对象
     * @param entity 要转换的Entity对象
     * @param clazz  转换后的POJO对象类型
     * @param <T>    转换后的POJO对象类型
     * @return 转换后的POJO对象
     * @throws Exception 如果转换过程中出现异常
     */
    public static <T> T entity2Pojo(Entity entity, Class<T> clazz) throws Exception {
        T t = clazz.newInstance();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            TableField tableField = field.getAnnotation(TableField.class);
            String column = ObjectUtil.isNotNull(tableField) && StrUtil.isNotBlank(tableField.value()) ? tableField.value() : field.getName();
            if (field.getType() == String.class) {
                field.set(t, entity.getStr(column));
            } else if (field.getType() == int.class || field.getType() == Integer.class) {
                field.set(t, entity.getInt(column));
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                field.set(t, entity.getBool(column));
            } else if (field.getType() == long.class || field.getType() == Long.class) {
                field.set(t, entity.getLong(column));
            } else if (field.getType() == double.class || field.getType() == Double.class) {
                field.set(t, entity.getDouble(column));
            } else if (field.getType() == Date.class) {
                field.set(t, entity.getDate(column));
            } else {
                // 使用json string转对象
                Object o = JSON.parseObject(entity.getStr(column), field.getType());
                if (ObjectUtil.isNotNull(o)) {
                    field.set(t, o);
                }
            }
        }
        return t;
    }

    /**
     * 将实体列表转换成指定类型的POJO对象列表
     * @param entityList 要转换的实体列表
     * @param clazz      转换后的POJO对象类型
     * @param <T>        转换后的POJO对象类型
     * @return 转换后的POJO对象列表
     * @throws RuntimeException 如果转换过程中出现异常
     */
    public static <T> List<T> entity2Pojo(List<Entity> entityList, Class<T> clazz) {
        return entityList.stream().map(entity -> {
            try {
                return entity2Pojo(entity, clazz);
            } catch (Exception e) {
                log.error("转换异常" + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 将POJO对象转换为Entity对象
     * @param pojo      待转换的POJO对象
     * @param clazz     POJO对象的Class类型
     * @param tableName 数据库表名
     * @return 转换后的Entity对象
     * @throws Exception 转换过程中可能抛出的异常
     */
    public static Entity pojo2Entity(Object pojo, Class clazz, String tableName) throws Exception {
        Field[] declaredFields = clazz.getDeclaredFields();
        Entity entity = Entity.create(tableName);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            TableField tableField = field.getAnnotation(TableField.class);
            String column = ObjectUtil.isNotNull(tableField) && StrUtil.isNotBlank(tableField.value()) ? tableField.value() : field.getName();
            entity.set(column, field.get(pojo));
        }
        return entity;
    }
}
