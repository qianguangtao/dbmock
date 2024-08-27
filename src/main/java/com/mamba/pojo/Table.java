package com.mamba.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/27 19:27
 * @description: TODO
 */
@Data
public class Table {
    private String name;
    private Integer total;

    private List<Column> columnList;
}
