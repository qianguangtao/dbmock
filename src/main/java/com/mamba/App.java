package com.mamba;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;

import java.io.InputStream;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        ClassPathResource cpr = new ClassPathResource("table.json");
        final Object parse = JSON.parse(cpr.readBytes(), Feature.SupportArrayToBean);
        String s = JSON.toJSONString(parse);
        System.out.println(s);
    }
}
