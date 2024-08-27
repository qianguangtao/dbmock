package com.mamba.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/27 21:41
 * @description: 表字段注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableField {
    /** value对应表列名 */
    String value() default "";
}
