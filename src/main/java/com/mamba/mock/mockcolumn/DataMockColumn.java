package com.mamba.mock.mockcolumn;

import com.mamba.config.MockConfig;
import com.mamba.mock.columnconfigdata.AbstractColumnConfigData;
import com.mamba.mock.columnconfigdata.ColumnConfigDataFactory;
import com.mamba.pojo.Column;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 20:43
 * @description: 配置数据列
 */
@Slf4j
public class DataMockColumn extends AbstractMockColumn {
    public DataMockColumn(Column column, int total, int index) {
        super(column, total, index);
    }

    @Override
    public String getColumnValue() {
        return calculateColumnValueByDataConfig(this.getColumn(), MockConfig.getInstance().getDependKeyMap());
    }

    /**
     * 处理table.json中的columnList[i].data
     * 4.1、columnList[i].data为String，则是取本表的另一个字段的值
     * 4.2、columnList[i].data为[]，则随机取个数，作为列值
     * 4.3、columnList[i].data为{}，则取start~end中间的随机值（目前只支持时间范围）
     * @param column
     * @param dependKeyMap
     * @return
     */
    public String calculateColumnValueByDataConfig(Column column, Map<String, String> dependKeyMap) {
        AbstractColumnConfigData columnConfigData = ColumnConfigDataFactory.getColumnConfigData(this);
        return columnConfigData.getColumnValueByDataConfig();
    }
}
