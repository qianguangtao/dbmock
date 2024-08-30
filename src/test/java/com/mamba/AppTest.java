package com.mamba;

import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mamba.pojo.Table;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 13:15
 * @description: TODO
 */
public class AppTest {

    public static void main(String[] args) {
        ClassPathResource cpr = new ClassPathResource("table2.json");
        String s = new String(cpr.readBytes());
        System.out.println(s);
        List<Table> tablesList = JSON.parseObject(s, new TypeReference<List<Table>>() {
        });
        System.out.println(tablesList);
    }
}
