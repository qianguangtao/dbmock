package com.mamba.pojo;

import lombok.Data;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/27 19:24
 * @description: TODO
 */
@Data
public class Column<T> {
    private String name;
    private String foreignKey;
    private Boolean required;
    private T data;
}
