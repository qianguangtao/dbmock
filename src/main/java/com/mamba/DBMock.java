package com.mamba;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.Entity;
import com.mamba.config.MockConfig;
import com.mamba.mock.mockcolumn.AbstractMockColumn;
import com.mamba.mock.mockcolumn.MockColumnFactory;
import com.mamba.pojo.Column;
import com.mamba.pojo.ColumnInfo;
import com.mamba.pojo.JdbcType;
import com.mamba.pojo.Table;
import com.mamba.service.TableService;
import com.mamba.util.DBConverter;
import com.mamba.util.PatternUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 21:18
 * @description: TODO
 */
@Slf4j
public class DBMock {
    public static void mock() {
        List<Table> tablesList = MockConfig.getInstance().getTablesList();
        resolveTablesListWithDB(tablesList);
        for (Table table : tablesList) {
            log.info("正在处理表：" + table.getName());
            List<Column> columnList = table.getColumnList();
            List<String> columnNameList = getInsertColumnName(columnList);
            // 表列的处理顺序 主键 > 普通列 > 有依赖的列
            Map<String, Column> columnMap = columnList.stream().collect(Collectors.toMap(Column::getName, c -> c));
            List<List<String>> valueListBatch = new ArrayList<>();
            for (int i = 1; i <= table.getTotal(); i++) {
                MockConfig.getInstance().getDependKeyMap().clear();
                List<String> valueList = calculateColumnValue(columnNameList, columnMap, Integer.valueOf(i), table.getTotal());
                valueListBatch.add(valueList);
            }
            TableService.batchInsert(table.getName(), columnNameList, valueListBatch);
            log.info("插入表：" + table.getName() + "成功");
        }
    }

    /**
     * 计算insert 语句的列值
     * @param columnNameList 列名list
     * @param columnMap      key=列名，value=Column封装的对象
     * @param index          当前构造insert语句的下标（for循环下标）
     * @param total          当前构造insert语句的总条数
     * @return
     */
    public static List<String> calculateColumnValue(List<String> columnNameList, Map<String, Column> columnMap, int index, int total) {
        List<String> valueList = new ArrayList<>();
        for (String columnName : columnNameList) {
            Column column = columnMap.get(columnName);
            AbstractMockColumn mockColumn = MockColumnFactory.getMockColumn(column, total, index);
            String columnValue = mockColumn.getColumnValue();
            MockConfig.getInstance().getDependKeyMap().put(columnName, columnValue);
            valueList.add(columnValue);
        }
        return valueList;
    }

    /**
     * 获取insert语句insert into table(column1, column2, column3)中的[column1, column2, column3]集合
     * 列排序规则：key > 普通列 > 依赖于其他列的列
     * @param columnList
     * @return
     */
    public static List<String> getInsertColumnName(List<Column> columnList) {
        List<String> columnNameList = new ArrayList<>();
        List<String> allColumnNameList = columnList.stream().map(Column::getName).collect(Collectors.toList());
        String key = columnList.stream().filter(Column::getIsKey).map(Column::getName).findFirst().get();
        columnNameList.add(key);
        // 获取当前列值依赖与其他列值的column，这种放在最后处理
        List<String> columnWithDepend = columnList.stream().filter(c -> ObjectUtil.isNotNull(c.getData()) && (c.getData() instanceof String)).map(Column::getName).collect(Collectors.toList());
        List<String> normalColumn = allColumnNameList.stream().filter(c -> !columnWithDepend.contains(c) && !c.equals(key)).collect(Collectors.toList());
        columnNameList.addAll(normalColumn);
        columnNameList.addAll(columnWithDepend);
        return columnNameList;
    }

    /**
     * 结合数据库中的表字典定义和table.json表字段定义，获取最终的表字段定义
     * @param tablesList
     * @throws Exception
     */
    public static void resolveTablesListWithDB(List<Table> tablesList) {
        for (Table table : tablesList) {
            List<Column> columnList = Optional.ofNullable(table.getColumnList()).orElse(new ArrayList<Column>());
            List<String> columnNameList = new ArrayList<String>();
            if (CollectionUtil.isNotEmpty(columnList)) {
                columnNameList = table.getColumnList().stream().map(Column::getName).collect(Collectors.toList());
            }
            List<Entity> tableInfo = TableService.getTableInfo((table.getName()));
            List<ColumnInfo> columnInfoList = DBConverter.entity2Pojo(tableInfo, ColumnInfo.class);
            for (ColumnInfo columnInfo : columnInfoList) {
                Pair<String, Integer> columnPair = PatternUtil.getColumnTypeAndSize(columnInfo.getType());
                JdbcType jdbcType = null;
                Integer length = null;
                if (ObjectUtil.isNotNull(columnPair)) {
                    jdbcType = JdbcType.of(columnPair.getKey());
                    length = columnPair.getValue();
                } else {
                    jdbcType = JdbcType.of(columnInfo.getType());
                }
                String field = columnInfo.getField();
                // 判断是否必填
                if (ObjectUtil.equals(columnInfo.getNullable(), "NO")) {
                    Boolean isKey = ObjectUtil.equals(columnInfo.getKey(), "PRI");
                    if (columnNameList.contains(field)) {
                        // 如果table.json已经有了列配置，使用数据库列定义更新列配置
                        for (Column c : table.getColumnList()) {
                            if (c.getName().equals(field)) {
                                c.setRequired(true);
                                c.setJdbcType(jdbcType);
                                c.setLength(length);
                                c.setIsKey(isKey);
                                break;
                            }
                        }
                    } else {
                        // 如果table.json没有列配置，使用数据库列定义新增列配置
                        Column column = new Column();
                        column.setName(field);
                        column.setRequired(true);
                        column.setJdbcType(jdbcType);
                        column.setLength(length);
                        column.setIsKey(isKey);
                        columnList.add(column);
                    }
                } else {
                    // 数据库可为空，table.json不能为空，需要使用数据库定义填充jdbcType和length
                    for (Column c : table.getColumnList()) {
                        if (c.getName().equals(field)) {
                            c.setJdbcType(jdbcType);
                            c.setLength(length);
                            break;
                        }
                    }
                }
            }
        }
    }
}
