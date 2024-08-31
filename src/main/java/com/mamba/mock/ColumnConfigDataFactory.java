package com.mamba.mock;

import cn.hutool.core.util.ObjectUtil;
import com.mamba.util.ReflectUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/31 10:24
 * @description: TODO
 */
public class ColumnConfigDataFactory {
    static {
        Map<String, AbstractColumnConfigData> map = new HashMap<>(16);
        // 取类包名.分割后的第一个作为待扫描包
        String packageName = ColumnConfigDataFactory.class.getName().split("\\.")[0];
        Set<Class<? extends AbstractColumnConfigData>> classSet = ReflectUtil.scanClassBySuper(packageName, AbstractColumnConfigData.class);
        classSet.forEach(clazz -> {
            AbstractColumnConfigData columnConfigData = null;
            try {
                columnConfigData = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (ObjectUtil.isNotNull(columnConfigData)) {
                map.put(columnConfigData.getDataClazz(), columnConfigData);
            }
        });
        beanMap = map;
    }

    static Map<String, AbstractColumnConfigData> beanMap;

    public static AbstractColumnConfigData getColumnConfigData(AbstractMockColumn mockColumn) {
        String dataClazz = mockColumn.getColumn().getData().getClass().getName();
        AbstractColumnConfigData columnConfigData = beanMap.get(dataClazz);
        if (ObjectUtil.isNull(columnConfigData)) {
            throw new RuntimeException("未找到对应的列数据配置类");
        }
        columnConfigData.setMockColumn(mockColumn);
        return columnConfigData;
    }
}
