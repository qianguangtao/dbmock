package com.mamba.mock.mockcolumn;

import com.mamba.pojo.Column;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 20:57
 * @description: TODO
 */
public class DefaultMockColumn extends AbstractMockColumn {
    public DefaultMockColumn(Column column, int total, int index) {
        super(column, total, index);
    }

    @Override
    public String getColumnValue() {
        return getDefaultColumnValue();
    }
}
