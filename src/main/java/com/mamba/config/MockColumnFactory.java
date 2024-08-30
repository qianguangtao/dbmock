package com.mamba.config;

import cn.hutool.core.util.ObjectUtil;
import com.mamba.mock.*;
import com.mamba.pojo.Column;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 21:03
 * @description: TODO
 */
public class MockColumnFactory {
    public static AbstractMockColumn getMockColumn(Column column, int total, int index) {
        if (column.getIsKey()) {
            return new PrimaryKeyMockColumn(column, total, index);
        } else if (ObjectUtil.isNotNull(column.getData())) {
            return new DataMockColumn(column, total, index);
        } else if (ObjectUtil.isNotNull(column.getForeignKey())) {
            return new ForeignKeyMockColumn(column, total, index);
        } else {
            return new DefaultMockColumn(column, total, index);
        }
    }
}
