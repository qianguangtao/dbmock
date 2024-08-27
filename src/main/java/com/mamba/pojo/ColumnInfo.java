package com.mamba.pojo;

import com.mamba.annotations.TableField;
import lombok.Data;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/27 21:37
 * @description: TODO
 */
@Data
public class ColumnInfo {
    private String field;
    private String type;
    @TableField("null")
    private String nullable;
    private String key;
    private String extra;
    private String privileges;
    private String comment;
}
