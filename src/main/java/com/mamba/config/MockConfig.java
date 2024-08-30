package com.mamba.config;

import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mamba.pojo.Table;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 17:16
 * @description: TODO
 */
@Data
public class MockConfig {
    private List<Table> tablesList;
    private Map<String, Integer> tableTotalMap;
    private ConcurrentHashMap<String, String> dependKeyMap;
    private MockConfigProperties mockConfigProperties;
    private Random columnRandom = new Random();

    private List<Table> getTableListFromFile(String fileClassPath) {
        ClassPathResource cpr = new ClassPathResource(fileClassPath);
        String s = new String(cpr.readBytes());
        return JSON.parseObject(s, new TypeReference<List<Table>>() {
        });
    }

    public static MockConfig getInstance() {
        return InstanceIdGeneratorHolder.instance;
    }

    private MockConfig() {
        mockConfigProperties = new MockConfigPropertiesBean();
        tablesList = getTableListFromFile(mockConfigProperties.getTableFile());
        tableTotalMap = tablesList.stream().collect(Collectors.toMap(Table::getName, t -> t.getTotal()));
        dependKeyMap = new ConcurrentHashMap<>();
    }

    private static class InstanceIdGeneratorHolder {
        static MockConfig instance = new MockConfig();
    }

}
