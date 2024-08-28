package com.mamba;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
                if (ObjectUtil.equals(columnInfo.getNullable(), "YES")) {
                    if (columnNameList.contains(field)) {
                        for (Column c : table.getColumnList()) {
                            if (c.getName().equals(field)) {
                                c.setRequired(true);
                                c.setJdbcType(jdbcType);
                                c.setLength(length);
                                break;
                            }
                        }
                    } else {
                        Column column = new Column();
                        column.setName(field);
                        column.setRequired(true);
                        column.setJdbcType(jdbcType);
                        column.setLength(length);
                        columnList.add(column);
                    }
                }
            }
            System.out.println("处理完之后的columnList");
            System.out.println(JSON.toJSONString(columnList, SerializerFeature.PrettyFormat));
        }
    }

    public static List<Entity> getTableInfo(String tableName) throws SQLException {
        String sql = "SHOW FULL COLUMNS FROM " + tableName;
        return Db.use().query(sql);
    }
}
