package com.mamba.mock.mockcolumn;

import com.mamba.pojo.Column;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 20:57
 * @description: 默认的列mock
 */
public class DefaultMockColumn extends AbstractMockColumn {
    public DefaultMockColumn(Column column, int total, int index) {
        super(column, total, index);
    }

    /**
     * 获取列值的字符串表示形式
     * @return 列值的字符串表示形式
     */
    @Override
    public String getColumnValue() {
        return getDefaultColumnValue();
    }
}
