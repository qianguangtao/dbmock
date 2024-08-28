package com.mamba.pojo;

import lombok.Data;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/27 19:24
 * @description: dbmock列配置
 */
@Data
public class Column<T> {
    private Boolean isKey = false;
    private String name;
    private String foreignKey;
    private Boolean required;
    private JdbcType jdbcType;
    private Integer length;
    private T data;
}
