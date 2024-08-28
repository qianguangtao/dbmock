package com.mamba;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.*;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mamba.pojo.Column;
import com.mamba.pojo.ColumnInfo;
import com.mamba.pojo.JdbcType;
import com.mamba.pojo.Table;
import com.mamba.util.DBConverter;
import com.mamba.util.PatternUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    static List<String> tables = Arrays.asList("base_user"
            , "rdwh_project_main"
            , "rdwh_bd_virtual_organize"
            , "rdwh_bd_virtual_organize_mapping"
            , "rdwh_work"
            , "rdwh_work_project_2024"
            , "rdwh_work_project_info_2024"
            , "rdwh_work_content"
            , "base_user_extension");

    /**
     * 1、读取table.json配置
     * 2、for循环配置表里的table
     * <p>
     * 3、根据table表名，查询表定义，根据表定义ColumnInfo中的nullable更新columnList[i]
     * 3.1、更新columnList[i].required
     * 3.2、维护columnList[i].jdbctype
     * 3.3、维护columnList[i].length
     * </p>
     * <p>
     * 4、step 3处理完，生成新的List<Table>，for循环生成insert语句，批量插入数据库
     * 4.1、columnList[i].data为数组，则随机取个数，作为列值
     * 4.2、columnList[i].data为String，则是取本表的另一个字段的值
     * 4.3、ColumnInfo中的nullable为‘NO’，则根据长度和类型，生成列值
     * 4.4、columnList[i].foreignKey不为空，则根据两个表total，平均分配外键id
     * </p>
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ClassPathResource cpr = new ClassPathResource("table3-single.json");
        final Object parse = JSON.parse(cpr.readBytes(), Feature.SupportArrayToBean);
        String s = JSON.toJSONString(parse);
        List<Table> tablesList = JSON.parseObject(s, new TypeReference<List<Table>>() {
        });
        resolveTablesListWithDB(tablesList);
        System.out.println("处理完之后的tablesList");
        System.out.println(JSON.toJSONString(tablesList, SerializerFeature.PrettyFormat));
        Map<String, Integer> tableTotalMap = tablesList.stream().collect(Collectors.toMap(Table::getName, t -> t.getTotal()));
        // 4、构造insert语句
        for (Table table : tablesList) {
            List<Column> columnList = table.getColumnList();
            List<String> columnNameList = new ArrayList<String>();
            List<String> allColumnNameList = columnList.stream().map(Column::getName).collect(Collectors.toList());
            String key = columnList.stream().filter(Column::getIsKey).map(Column::getName).findFirst().get();
            columnNameList.add(key);
            // 获取当前列值依赖与其他列值的column，这种放在最后处理
            List<String> columnWithDepend = columnList.stream().filter(c -> ObjectUtil.isNotNull(c.getData()) && (c.getData() instanceof String)).map(Column::getName).collect(Collectors.toList());
            List<String> normalColumn = allColumnNameList.stream().filter(c -> !columnWithDepend.contains(c) && !c.equals(key)).collect(Collectors.toList());
            columnNameList.addAll(normalColumn);
            columnNameList.addAll(columnWithDepend);
            // 表列的处理顺序 主键 > 普通列 > 有依赖的列
            System.out.println("columnNameList排序后：" + columnNameList);
            Map<String, Column> columnMap = columnList.stream().collect(Collectors.toMap(Column::getName, c -> c));
            List<List<String>> valueListBatch = new ArrayList<>();
            for (int i = 1; i <= table.getTotal(); i++) {
                List<String> valueList = new ArrayList<>();
                int finalI = i;
                Map<String, String> foreignKeyMap = new HashMap<>();
                columnNameList.forEach(columnName -> {
                    String columnValue = null;
                    Column column = columnMap.get(columnName);
                    if (column.getIsKey()) {
                        columnValue = getColumnValue(finalI, column);
                    } else if (ObjectUtil.isNotNull(column.getData())) {
                        if (column.getData() instanceof String) {
                            // column.getData()是String，则是取该表的其他字段值
                            String data = (String) column.getData();
                            columnValue = foreignKeyMap.get(data);
                        } else if (column.getData() instanceof JSONArray) {
                            // column.getData()是数组，则随机取值
                            JSONArray jsonArray = (JSONArray) column.getData();
                            Object[] random = jsonArray.stream().toArray();
                            columnValue = getColumnValue(random[new Random().nextInt(random.length)], column);
                        } else if (column.getData() instanceof JSONObject) {
                            // column.getData()是JSONObject，取开始-结束的随机值
                            JSONObject jsonObject = (JSONObject) column.getData();
                            String start = jsonObject.getString("start");
                            String end = jsonObject.getString("end");
                            if (!StrUtil.hasBlank(start, end)) {
                                String datePattern = null;
                                if (column.getJdbcType() == JdbcType.DATE) {
                                    // 处理日期，取当前日期
                                    datePattern = DatePattern.NORM_DATE_PATTERN;
                                } else {
                                    datePattern = DatePattern.NORM_DATETIME_PATTERN;
                                }
                                DateTime startTime = DateUtil.parse(start, datePattern);
                                DateTime endTime = DateUtil.parse(end, datePattern);
                                long between = DateUtil.between(startTime, endTime, DateUnit.DAY);
                                DateTime dateTime = startTime.offsetNew(DateField.DAY_OF_YEAR, new Random().nextInt(Convert.toInt(between + 1)));
                                columnValue = getColumnValue(DateUtil.format(dateTime, datePattern), column);
                            }
                        }
                    } else if (ObjectUtil.isNotNull(column.getForeignKey())) {
                        // 处理字段是另一个表的主键，这里table和fkTable是n:1的关系，将fkTable的主键（total）平均分配到table
                        String fkTable = column.getForeignKey().split("\\.")[0];
                        Integer fkTableTotal = tableTotalMap.get(fkTable);
                        int percent = table.getTotal() / fkTableTotal;
                        int data = (finalI - 1) / percent + 1;
                        columnValue = getColumnValue(data, column);
                    } else {
                        // 处理普通的非空字段
                        columnValue = getDefaultColumnValue(column);
                    }
                    foreignKeyMap.put(columnName, columnValue);
                    valueList.add(columnValue);
                });
                valueListBatch.add(valueList);
            }
            String insertStatement = buildInsertStatement(table.getName(), columnNameList, valueListBatch);
            System.out.println("生成insert语句：" + insertStatement);
        }
    }

    public static String getDefaultColumnValue(Column column) {
        String columnValue = null;
        if (column.getJdbcType().clazz == LocalDate.class) {
            return "'" + DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN) + "'";
        } else if (column.getJdbcType().clazz == LocalDateTime.class) {
            return "'" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN) + "'";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < column.getLength(); i++) {
                sb.append("1");
            }
            columnValue = sb.toString();
        }
        return getColumnValue(columnValue, column);
    }

    public static String getColumnValue(Object value, Column column) {
        if (column.getJdbcType().clazz == String.class
                || (column.getJdbcType().clazz == LocalDate.class)
                || (column.getJdbcType().clazz == LocalDateTime.class)) {
            return "'" + Convert.toStr(value) + "'";
        } else {
            return Convert.toStr(value);
        }
    }

    /**
     * 结合数据库中的表字典定义和table.json表字段定义，获取最终的表字段定义
     * @param tablesList
     * @throws Exception
     */
    public static void resolveTablesListWithDB(List<Table> tablesList) throws Exception {
        for (Table table : tablesList) {
            List<Column> columnList = Optional.ofNullable(table.getColumnList()).orElse(new ArrayList<Column>());
            List<String> columnNameList = new ArrayList<String>();
            if (CollectionUtil.isNotEmpty(columnList)) {
                columnNameList = table.getColumnList().stream().map(Column::getName).collect(Collectors.toList());
            }
            List<Entity> tableInfo = getTableInfo((table.getName()));
            List<ColumnInfo> columnInfoList = DBConverter.entity2Pojo(tableInfo, ColumnInfo.class);
            // System.out.println(JSON.toJSONString(columnInfoList, SerializerFeature.PrettyFormat));
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

    public static List<Entity> getTableInfo(String tableName) throws SQLException {
        String sql = "SHOW FULL COLUMNS FROM " + tableName;
        return Db.use().query(sql);
    }
}
