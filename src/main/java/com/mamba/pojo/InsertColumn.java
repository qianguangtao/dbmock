package com.mamba.pojo;

import lombok.Data;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/27 22:39
 * @description: TODO
 */
@Data
public class InsertColumn<T> {
    private Boolean isKey;
    private String name;
    private T value;
}
