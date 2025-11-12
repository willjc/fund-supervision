package com.ruoyi.service;

import com.ruoyi.domain.PensionCheckinDTO;

/**
 * 养老机构入驻Service接口
 *
 * @author ruoyi
 * @date 2025-11-11
 */
public interface IPensionCheckinService
{
    /**
     * 创建入驻申请
     * 一次性完成以下操作:
     * 1. 创建elder_info老人信息记录
     * 2. 创建bed_allocation床位分配记录
     * 3. 创建order_info订单记录
     * 4. 创建order_item费用明细记录(服务费、押金、会员费)
     * 5. 更新bed_info床位状态为占用
     *
     * @param dto 入驻信息DTO
     * @param userId 当前用户ID
     * @return 结果
     */
    public int createCheckin(PensionCheckinDTO dto, Long userId);
}
