package com.mamba;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.mamba.pojo.Table;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App
{
    static List<String> tables = Arrays.asList("base_user"
            ,"rdwh_project_main"
            ,"rdwh_bd_virtual_organize"
            ,"rdwh_bd_virtual_organize_mapping"
            ,"rdwh_work"
            ,"rdwh_work_project_2024"
            ,"rdwh_work_project_info_2024"
            ,"rdwh_work_content"
            ,"base_user_extension");
    public static void main( String[] args )
    {
        ClassPathResource cpr = new ClassPathResource("table2.json");
        final Object parse = JSON.parse(cpr.readBytes(), Feature.SupportArrayToBean);
        String s = JSON.toJSONString(parse);
        List<Table> tablesList = JSON.parseObject(s, new TypeReference<List<Table>>() {
        });
        tablesList.stream().forEach(table -> {
            System.out.println(table.getName());
            if (CollectionUtil.isNotEmpty(table.getColumnList())) {
                table.getColumnList().stream().forEach(column -> System.out.println(column));
            }
            System.out.println("================================");
        });
    }
}
