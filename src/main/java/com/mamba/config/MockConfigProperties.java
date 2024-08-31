package com.mamba.config;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/8/30 17:16
 * @description: MockConfigProperties接口
 */
public interface MockConfigProperties {
    /**
     * 获取ID前缀
     * @return 返回ID前缀的字符串
     */
    String getIdPrefix();

    /**
     * 获取批量处理大小。
     * @return 批量处理大小的整数值
     */
    Integer getBatchSize();

    /**
     * 获取表格文件路径。
     * @return 表格文件路径的字符串表示
     */
    String getTableFile();
}
