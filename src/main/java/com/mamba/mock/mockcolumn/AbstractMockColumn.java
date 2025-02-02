package com.mamba.mock.mockcolumn;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.mamba.pojo.Column;
import com.mamba.pojo.JdbcType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 17:13
 * @description: Mock列数据的封装抽象类
 */
@Data
public abstract class AbstractMockColumn {
    private Column column;
    private int total;
    private int index;

    /**
     * 获取列值
     * @return 返回列值的字符串形式
     */
    public abstract String getColumnValue();

    /**
     * 获取列值的insert values表示形式（字符串有''）
     * @param value 列值
     * @return 列值的字符串表示形式
     */
    public String getColumnValue(Object value) {
        if (this.getColumn().getJdbcType().clazz == String.class
                || (this.getColumn().getJdbcType().clazz == LocalDate.class)
                || (this.getColumn().getJdbcType().clazz == LocalDateTime.class)) {
            return "'" + Convert.toStr(value) + "'";
        } else {
            return Convert.toStr(value);
        }
    }

    /**
     * 获取列默认值的字符串表示形式
     * @return 列默认值的字符串表示形式
     */
    public String getDefaultColumnValue() {
        String columnValue;
        if (this.getColumn().getJdbcType().clazz == LocalDate.class) {
            return "'" + DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN) + "'";
        } else if (this.getColumn().getJdbcType().clazz == LocalDateTime.class) {
            return "'" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN) + "'";
        } else if (this.getColumn().getJdbcType() == JdbcType.TEXT) {
            return "'" + this.getColumn().getName() + "'";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.getColumn().getLength(); i++) {
                sb.append("1");
            }
            columnValue = sb.toString();
        }
        return getColumnValue(columnValue);
    }

    public AbstractMockColumn(Column column, int total, int index) {
        this.column = column;
        this.total = total;
        this.index = index;
    }
}
