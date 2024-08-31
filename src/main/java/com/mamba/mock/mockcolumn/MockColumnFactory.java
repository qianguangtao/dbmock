package com.mamba.mock.mockcolumn;

import cn.hutool.core.util.ObjectUtil;
import com.mamba.pojo.Column;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 21:03
 * @description: MockColumn工厂类
 */
public class MockColumnFactory {
    /**
     * 根据给定的列、总数和索引返回一个抽象的模拟列对象。
     * @param column 给定的列对象
     * @param total  给定的总数
     * @param index  给定的索引
     * @return 返回一个抽象的模拟列对象
     */
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
