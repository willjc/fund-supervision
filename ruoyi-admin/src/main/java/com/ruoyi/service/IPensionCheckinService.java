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

    /**
     * 办理退住（强校验）：无待支付订单且账户三余额清零后方可退住。
     * 动作：床位分配转"已退住"、释放床位、老人档案转"已退住"。
     *
     * @param elderId 老人ID
     * @param institutionId 机构ID
     * @param operator 操作人
     * @param currentUserId 当前用户（数据范围校验，admin 传 null）
     * @return 结果
     */
    public int checkoutElder(Long elderId, Long institutionId, String operator, Long currentUserId);
}
