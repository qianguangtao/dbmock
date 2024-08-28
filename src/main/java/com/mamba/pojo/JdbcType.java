package com.mamba.pojo;

import cn.hutool.core.map.SafeConcurrentHashMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/28 8:33
 * @description: JdbcType
 */
public enum JdbcType {
    /***/
    TINYINT("tinyint", Integer.class),
    INTEGER("int", Integer.class),
    BIGINT("bigint", Long.class),
    FLOAT("float", Float.class),
    DOUBLE("double", Double.class),
    NUMERIC("numeric", Double.class),
    DECIMAL("decimal", Double.class),
    CHAR("char", String.class),
    VARCHAR("varchar", String.class),
    TEXT("text", String.class),
    LONGTEXT("longtext", String.class),
    DATE("date", LocalDate.class),
    TIME("time", LocalDateTime.class),
    DATETIME("datetime", LocalDateTime.class),
    TIMESTAMP("timestamp", LocalDateTime.class),
    ;

    public final String typeCode;
    public final Class clazz;

    /**
     * 构造
     * @param code {@link java.sql.Types} 中对应的值
     */
    JdbcType(String code, Class clazz) {
        this.typeCode = code;
        this.clazz = clazz;
    }

    private static final Map<String, JdbcType> CODE_MAP = new SafeConcurrentHashMap<>(100, 1);

    static {
        for (JdbcType type : JdbcType.values()) {
            CODE_MAP.put(type.typeCode, type);
        }
    }

    /**
     * 通过{@link java.sql.Types}中对应int值找到enum值
     * @param code Jdbc type值
     * @return {@code JdbcType}
     */
    public static JdbcType of(String code) {
        return CODE_MAP.get(code);
    }
}
