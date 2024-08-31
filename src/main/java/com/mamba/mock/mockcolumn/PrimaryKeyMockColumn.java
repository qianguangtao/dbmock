package com.mamba.mock.mockcolumn;

import com.mamba.config.MockConfig;
import com.mamba.config.MockConfigProperties;
import com.mamba.pojo.Column;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 20:39
 * @description: 主键列
 */
public class PrimaryKeyMockColumn extends AbstractMockColumn {
    @Override
    public String getColumnValue() {
        MockConfigProperties properties = MockConfig.getInstance().getMockConfigProperties();
        return getColumnValue(properties.getIdPrefix() + this.getIndex());
    }

    public PrimaryKeyMockColumn(Column column, int total, int index) {
        super(column, total, index);
    }
}
