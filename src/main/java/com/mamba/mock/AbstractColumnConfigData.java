package com.mamba.mock;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/31 9:49
 * @description: TODO
 */
public abstract class AbstractColumnConfigData {
    /**
     * 获取数据类型的Class全路径。
     * @return 返回数据类型的Class全路径
     */
    abstract public String getDataClazz();

    @Getter
    @Setter
    protected AbstractMockColumn mockColumn;

    /**
     * 获取列的原始值。
     * @return 返回列的原始值的字符串表示形式。
     */
    abstract public String getColumnValueByDataConfig();

    public AbstractColumnConfigData() {
        super();
    }
}
