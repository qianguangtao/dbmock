package com.mamba.mock.columnconfigdata;

import com.alibaba.fastjson.JSONArray;
import com.mamba.config.MockConfig;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/31 9:49
 * @description: TODO
 */
public class JSONArrayColumnConfigData extends AbstractColumnConfigData {

    public JSONArrayColumnConfigData() {
        super();
    }

    @Override
    public String getDataClazz() {
        return JSONArray.class.getName();
    }

    @Override
    public String getColumnValueByDataConfig() {
        JSONArray jsonArray = (JSONArray) this.mockColumn.getColumn().getData();
        Object[] random = jsonArray.stream().toArray();
        return this.mockColumn.getColumnValue(random[MockConfig.getInstance().getColumnRandom().nextInt(random.length)]);
    }
}
