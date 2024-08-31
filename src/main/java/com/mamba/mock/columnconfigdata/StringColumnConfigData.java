package com.mamba.mock.columnconfigdata;

import com.mamba.config.MockConfig;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/31 9:49
 * @description: String类型的ColumnConfigData
 */
public class StringColumnConfigData extends AbstractColumnConfigData {

    public StringColumnConfigData() {
        super();
    }

    @Override
    public String getDataClazz() {
        return String.class.getName();
    }

    @Override
    public String getColumnValueByDataConfig() {
        return MockConfig.getInstance().getDependKeyMap().get((String) this.mockColumn.getColumn().getData());
    }
}
