package com.mamba.config;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 19:43
 * @description: TODO
 */
public class MockConfigPropertiesBean implements MockConfigProperties {

    private final String idType;
    private final Integer batchSize;
    private final String tableFile;

    public MockConfigPropertiesBean() {
        ClassPathResource cpr = new ClassPathResource("application.properties");
        InputStream inputStream = cpr.getStream();
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            idType = StrUtil.isNotBlank(properties.getProperty("dbmock.idType")) ? properties.getProperty("dbmock.idType").trim() : "String";
            batchSize = StrUtil.isNotBlank(properties.getProperty("dbmock.batchSize")) ? Integer.parseInt(properties.getProperty("dbmock.batchSize").trim()) : 1000;
            tableFile = StrUtil.isNotBlank(properties.getProperty("dbmock.tableFile")) ? properties.getProperty("dbmock.tableFile").trim() : "table.json";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getIdPrefix() {
        if ("String".equals(idType)) {
            return IdUtil.simpleUUID().substring(0, 5) + "-";
        } else {
            return "1";
        }
    }

    @Override
    public Integer getBatchSize() {
        return batchSize;
    }

    @Override
    public String getTableFile() {
        return tableFile;
    }
}
