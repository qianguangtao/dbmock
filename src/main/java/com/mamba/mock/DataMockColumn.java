package com.mamba.mock;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.*;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mamba.config.MockConfig;
import com.mamba.pojo.Column;
import com.mamba.pojo.JdbcType;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Random;

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
        Random columnRandom = MockConfig.getInstance().getColumnRandom();
        if (column.getData() instanceof String) {
            // column.getData()是String，则是取该表的其他字段值
            String data = (String) column.getData();
            return dependKeyMap.get(data);
        } else if (column.getData() instanceof JSONArray) {
            // column.getData()是数组，则随机取值
            JSONArray jsonArray = (JSONArray) column.getData();
            Object[] random = jsonArray.stream().toArray();
            return this.getColumnValue(random[columnRandom.nextInt(random.length)]);
        } else if (column.getData() instanceof JSONObject) {
            // column.getData()是JSONObject，取开始-结束的随机值
            JSONObject jsonObject = (JSONObject) column.getData();
            String start = jsonObject.getString("start");
            String end = jsonObject.getString("end");
            if (!StrUtil.hasBlank(start, end)) {
                String datePattern;
                if (column.getJdbcType() == JdbcType.DATE) {
                    // 处理日期，取当前日期
                    datePattern = DatePattern.NORM_DATE_PATTERN;
                } else {
                    datePattern = DatePattern.NORM_DATETIME_PATTERN;
                }
                DateTime startTime = DateUtil.parse(start, datePattern);
                DateTime endTime = DateUtil.parse(end, datePattern);
                long between = DateUtil.between(startTime, endTime, DateUnit.DAY);
                DateTime dateTime = startTime.offsetNew(DateField.DAY_OF_YEAR, columnRandom.nextInt(Convert.toInt(between + 1)));
                return this.getColumnValue(DateUtil.format(dateTime, datePattern));
            }
        } else {
            log.error("Invalid column value");
            throw new IllegalArgumentException("Invalid column value");
        }
        return null;
    }
}
