package com.mamba.mock.columnconfigdata;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.*;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.mamba.config.MockConfig;
import com.mamba.pojo.JdbcType;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/31 9:49
 * @description: TODO
 */
public class JSONObjectColumnConfigData extends AbstractColumnConfigData {

    public JSONObjectColumnConfigData() {
        super();
    }

    @Override
    public String getDataClazz() {
        return JSONObject.class.getName();
    }

    @Override
    public String getColumnValueByDataConfig() {
        // column.getData()是JSONObject，取开始-结束的随机值
        JSONObject jsonObject = (JSONObject) this.mockColumn.getColumn().getData();
        String start = jsonObject.getString("start");
        String end = jsonObject.getString("end");
        if (StrUtil.hasBlank(start, end)) {
            throw new IllegalArgumentException("start和end不能为空");
        }
        String datePattern;
        if (this.mockColumn.getColumn().getJdbcType() == JdbcType.DATE) {
            // 处理日期，取当前日期
            datePattern = DatePattern.NORM_DATE_PATTERN;
        } else {
            datePattern = DatePattern.NORM_DATETIME_PATTERN;
        }
        DateTime startTime = DateUtil.parse(start, datePattern);
        DateTime endTime = DateUtil.parse(end, datePattern);
        long between = DateUtil.between(startTime, endTime, DateUnit.DAY);
        DateTime dateTime = startTime.offsetNew(DateField.DAY_OF_YEAR, MockConfig.getInstance().getColumnRandom().nextInt(Convert.toInt(between + 1)));
        return this.mockColumn.getColumnValue(DateUtil.format(dateTime, datePattern));
    }
}
