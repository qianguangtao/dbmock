package com.mamba.config;

import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mamba.pojo.Table;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 17:16
 * @description: mock配置类
 */
@Data
public class MockConfig {
    private List<Table> tablesList;
    private Map<String, Integer> tableTotalMap;
    private ConcurrentHashMap<String, String> dependKeyMap;
    private MockConfigProperties mockConfigProperties;
    private Random columnRandom = new Random();

    /**
     * 从文件中获取表格列表
     * @param fileClassPath 文件的类路径
     * @return 包含表格对象的列表
     * @throws IOException 如果文件读取失败
     */
    private List<Table> getTableListFromFile(String fileClassPath) {
        ClassPathResource cpr = new ClassPathResource(fileClassPath);
        String s = new String(cpr.readBytes());
        return JSON.parseObject(s, new TypeReference<List<Table>>() {
        });
    }

    /**
     * 获取MockConfig的实例对象。
     * @return 返回MockConfig的实例对象
     */
    public static MockConfig getInstance() {
        return InstanceIdGeneratorHolder.instance;
    }

    /**
     * 构造方法，私有构造函数，防止外部实例化。
     * 初始化mockConfigProperties对象，从文件中读取表格列表，并构建表格总数映射表和依赖关系映射表。
     */
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
