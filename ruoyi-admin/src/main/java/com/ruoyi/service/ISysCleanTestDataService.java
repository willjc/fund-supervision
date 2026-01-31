package com.ruoyi.service;

import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 清除测试数据服务接口
 *
 * @author ruoyi
 */
public interface ISysCleanTestDataService
{
    /**
     * 清除所有测试数据
     *
     * @return 清除结果
     */
    AjaxResult cleanAllTestData();

    /**
     * 获取当前测试数据统计
     *
     * @return 统计信息
     */
    AjaxResult getTestDataStatus();
}
