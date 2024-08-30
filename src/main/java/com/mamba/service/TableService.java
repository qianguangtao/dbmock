package com.mamba.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.mamba.config.MockConfig;
import com.mamba.config.MockConfigProperties;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 17:09
 * @description: TODO
 */
@Slf4j
public class TableService {
    /**
     * 批量插入数据
     * @param tableName
     * @param columnNameList
     * @param valueListBatch
     */
    public static void batchInsert(String tableName, List<String> columnNameList, List<List<String>> valueListBatch) {
        MockConfigProperties mockConfigProperties = MockConfig.getInstance().getMockConfigProperties();
        List<List<List<String>>> partition = ListUtil.partition(valueListBatch, mockConfigProperties.getBatchSize());
        for (List<List<String>> list : partition) {
            String insertStatement = buildInsertStatement(tableName, columnNameList, list);
            try {
                Db.use().execute(insertStatement);
            } catch (SQLException e) {
                log.error("插入表：" + tableName + "失败" + e.getMessage(), e);
            }
        }
    }

    /**
     * 构造insert语句
     * @param tableName
     * @param columnName
     * @param columnValues
     * @return
     */
    public static String buildInsertStatement(String tableName, List<String> columnName, List<List<String>> columnValues) {
        // 构建列名部分
        StringBuilder columns = new StringBuilder("(");
        for (int i = 0; i < columnName.size(); i++) {
            columns.append(columnName.get(i));
            if (i < columnName.size() - 1) {
                columns.append(", ");
            }
        }
        columns.append(")");
        // 构建值部分
        StringBuilder values = new StringBuilder("VALUES ");
        for (int i = 0; i < columnValues.size(); i++) {
            List<String> valuesRow = columnValues.get(i);
            values.append("(");
            for (int j = 0; j < valuesRow.size(); j++) {
                values.append(valuesRow.get(j));
                if (j < valuesRow.size() - 1) {
                    values.append(", ");
                }
            }
            values.append(")");
            if (i < columnValues.size() - 1) {
                values.append(", ");
            }
        }
        // 构造完整的 SQL 插入语句
        return "INSERT INTO " + tableName + " " + columns + " " + values + ";";
    }

    /**
     * 获取表信息
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static List<Entity> getTableInfo(String tableName) {
        String sql = "SHOW FULL COLUMNS FROM " + tableName;
        try {
            return Db.use().query(sql);
        } catch (SQLException e) {
            log.error("获取表信息失败" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
