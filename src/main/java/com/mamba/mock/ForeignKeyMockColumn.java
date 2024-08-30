package com.mamba.mock;

import com.mamba.config.MockConfig;
import com.mamba.config.MockConfigProperties;
import com.mamba.pojo.Column;

import java.util.Map;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 20:51
 * @description: TODO
 */
public class ForeignKeyMockColumn extends AbstractMockColumn {
    public ForeignKeyMockColumn(Column column, int total, int index) {
        super(column, total, index);
    }

    @Override
    public String getColumnValue() {
        Map<String, Integer> tableTotalMap = MockConfig.getInstance().getTableTotalMap();
        MockConfigProperties properties = MockConfig.getInstance().getMockConfigProperties();
        // 处理字段是另一个表的主键，这里table和fkTable是n:1的关系，将fkTable的主键（total）平均分配到table
        String fkTable = this.getColumn().getForeignKey().split("\\.")[0];
        Integer fkTableTotal = tableTotalMap.get(fkTable);
        int percent = this.getTotal() / fkTableTotal;
        int data = (this.getIndex() - 1) / percent + 1;
        return getColumnValue(properties.getIdPrefix() + data);
    }
}
