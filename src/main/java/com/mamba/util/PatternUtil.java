package com.mamba.util;

import cn.hutool.core.lang.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/27 22:54
 * @description: TODO
 */
public class PatternUtil {

    public static Pair<String, Object> getColumnTypeAndSize(String columnType) {
        // 正则表达式
        String regex = "(\\w+)\\((\\d+)\\)";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(columnType);
        // 检查是否匹配
        if (matcher.find()) {
            // 提取匹配的结果
            String prefix = matcher.group(1);
            int number = Integer.parseInt(matcher.group(2));
            return Pair.of(prefix, number);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getColumnTypeAndSize("datetime"));
    }
}
