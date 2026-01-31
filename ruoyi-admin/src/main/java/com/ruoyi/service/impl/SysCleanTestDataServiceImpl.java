package com.ruoyi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.service.ISysCleanTestDataService;

/**
 * 清除测试数据服务实现
 *
 * @author ruoyi
 */
@Service
public class SysCleanTestDataServiceImpl implements ISysCleanTestDataService
{
    private static final Logger log = LoggerFactory.getLogger(SysCleanTestDataServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 清除所有测试数据
     *
     * @return 清除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult cleanAllTestData()
    {
        List<String> messages = new ArrayList<>();

        try
        {
            // 1. 清除订单相关（从子表到主表）
            messages.add(cleanTable("DELETE FROM refund_record", "退款记录"));
            messages.add(cleanTable("DELETE FROM payment_record", "支付记录"));
            messages.add(cleanTable("DELETE FROM order_item", "订单明细"));
            messages.add(cleanTable("DELETE FROM order_info", "订单信息"));

            // 2. 清除押金相关
            messages.add(cleanTable("DELETE FROM deposit_apply", "押金申请"));

            // 3. 清除资金划拨相关
            messages.add(cleanTable("DELETE FROM supervision_account_log", "监管账户日志"));
            messages.add(cleanTable("DELETE FROM fund_transfer_apply_detail", "划拨申请明细"));
            messages.add(cleanTable("DELETE FROM fund_transfer_apply", "划拨申请"));
            messages.add(cleanTable("DELETE FROM fund_transfer_detail", "划拨明细"));
            messages.add(cleanTable("DELETE FROM fund_transfer", "资金划拨"));

            // 4. 清除账户相关
            messages.add(cleanTable("DELETE FROM balance_warning", "余额预警"));
            messages.add(cleanTable("DELETE FROM transaction_record", "交易记录"));
            messages.add(cleanTable("DELETE FROM account_balance_log", "账户余额日志"));
            messages.add(cleanTable("DELETE FROM account_info", "账户信息"));

            // 5. 清除入住相关（注意外键约束顺序）
            messages.add(cleanTable("DELETE FROM elder_check_in", "入住记录"));
            messages.add(cleanTable("DELETE FROM bed_allocation", "床位分配"));
            messages.add(cleanTable("DELETE FROM elder_photo", "老人照片"));
            messages.add(cleanTable("DELETE FROM elder_attachment", "老人附件"));

            // 6. 清除家属关联
            messages.add(cleanTable("DELETE FROM elder_family", "家属关联"));

            // 7. 清除其他业务数据
            messages.add(cleanTable("DELETE FROM pension_complaint", "投诉建议"));
            messages.add(cleanTable("DELETE FROM institution_review", "评价记录"));
            messages.add(cleanTable("DELETE FROM institution_visit_reservation", "预约记录"));
            messages.add(cleanTable("DELETE FROM expense_record", "费用记录"));
            messages.add(cleanTable("DELETE FROM user_favorite", "用户收藏"));
            messages.add(cleanTable("DELETE FROM release_supervision", "解除监管申请"));

            // 8. 清除老人入住状态
            messages.add(cleanElderStatus());

            // 9. 清除床位占用状态
            messages.add(cleanBedStatus());

            // 10. 清除测试用户（保留admin和机构管理员）
            messages.add(cleanTestUsers());

            // 11. 清除系统日志
            messages.add(cleanSystemLogs());

            return AjaxResult.success("测试数据清除完成", messages);
        }
        catch (Exception e)
        {
            log.error("清除测试数据失败", e);
            throw new ServiceException("清除测试数据失败: " + e.getMessage());
        }
    }

    /**
     * 清除表数据
     */
    private String cleanTable(String sql, String tableDesc)
    {
        try
        {
            int count = jdbcTemplate.update(sql);
            return String.format("✓ %s：清除 %d 条", tableDesc, count);
        }
        catch (Exception e)
        {
            log.warn("清除{}失败: {}", tableDesc, e.getMessage());
            return String.format("✗ %s：清除失败 - %s", tableDesc, e.getMessage());
        }
    }

    /**
     * 清除老人入住状态
     */
    private String cleanElderStatus()
    {
        try
        {
            String sql = "UPDATE elder_info SET status = '2' WHERE status = '1'";
            int count = jdbcTemplate.update(sql);
            return String.format("✓ 老人入住状态：更新 %d 条为已出院", count);
        }
        catch (Exception e)
        {
            log.warn("清除老人入住状态失败: {}", e.getMessage());
            return String.format("✗ 老人入住状态：清除失败 - %s", e.getMessage());
        }
    }

    /**
     * 清除床位��用状态
     */
    private String cleanBedStatus()
    {
        try
        {
            // 把所有非空置(0)、非维修(2)的床位都设为空置
            // 字典配置: 0=空置(空闲), 1=占用, 2=维修
            String sql = "UPDATE bed_info SET bed_status = '0' WHERE bed_status NOT IN ('0', '2')";
            int count = jdbcTemplate.update(sql);
            return String.format("✓ 床位状态：更新 %d 条为空置", count);
        }
        catch (Exception e)
        {
            log.warn("清除床位状态失败: {}", e.getMessage());
            return String.format("✗ 床位状态：清除失败 - %s", e.getMessage());
        }
    }

    /**
     * 清除测试用户（保留admin和机构管理员）
     */
    private String cleanTestUsers()
    {
        try
        {
            // 删除测试用户的角色关联
            String deleteUserRoleSql =
                "DELETE FROM sys_user_role WHERE user_id NOT IN (1) " +
                "AND user_id NOT IN (SELECT user_id FROM sys_user_role WHERE role_id = 100)";
            int roleCount = jdbcTemplate.update(deleteUserRoleSql);

            // 删除测试用户的岗位关联
            String deleteUserPostSql =
                "DELETE FROM sys_user_post WHERE user_id NOT IN (1) " +
                "AND user_id NOT IN (SELECT user_id FROM sys_user_role WHERE role_id = 100)";
            int postCount = jdbcTemplate.update(deleteUserPostSql);

            // 删除测试用户的机构关联
            String deleteUserInstSql =
                "DELETE FROM sys_user_institution WHERE user_id NOT IN (1) " +
                "AND user_id NOT IN (SELECT user_id FROM sys_user_role WHERE role_id = 100)";
            int instCount = jdbcTemplate.update(deleteUserInstSql);

            // 删除测试用户
            String deleteUserSql =
                "DELETE FROM sys_user WHERE user_id NOT IN (1) " +
                "AND user_id NOT IN (SELECT user_id FROM (SELECT user_id FROM sys_user_role WHERE role_id = 100) AS tmp)";
            int userCount = jdbcTemplate.update(deleteUserSql);

            return String.format("✓ 测试用户：删除 %d 条用户、%d 条角色关联、%d 条岗位关联、%d 条机构关联（保留admin和机构管理员）",
                userCount, roleCount, postCount, instCount);
        }
        catch (Exception e)
        {
            log.warn("清除测试用户失败: {}", e.getMessage());
            return String.format("✗ 测试用户：清除失败 - %s", e.getMessage());
        }
    }

    /**
     * 清除系统日志
     */
    private String cleanSystemLogs()
    {
        try
        {
            int operCount = jdbcTemplate.update("DELETE FROM sys_oper_log");
            int loginCount = jdbcTemplate.update("DELETE FROM sys_logininfor");
            return String.format("✓ 系统日志：操作日志 %d 条，登录日志 %d 条", operCount, loginCount);
        }
        catch (Exception e)
        {
            log.warn("清除系统日志失败: {}", e.getMessage());
            return String.format("✗ 系统日志：清除失败 - %s", e.getMessage());
        }
    }

    /**
     * 获取当前测试数���统计
     */
    @Override
    public AjaxResult getTestDataStatus()
    {
        Map<String, Object> stats = new HashMap<>();

        stats.put("elderCount", getCount("SELECT COUNT(*) FROM elder_info"));
        stats.put("elderDischargedCount", getCount("SELECT COUNT(*) FROM elder_info WHERE status = '2'"));
        stats.put("orderCount", getCount("SELECT COUNT(*) FROM order_info"));
        stats.put("depositCount", getCount("SELECT COUNT(*) FROM deposit_apply"));
        stats.put("accountCount", getCount("SELECT COUNT(*) FROM account_info"));
        stats.put("bedTotalCount", getCount("SELECT COUNT(*) FROM bed_info"));
        stats.put("bedOccupiedCount", getCount("SELECT COUNT(*) FROM bed_info WHERE bed_status = '2'"));
        stats.put("complaintCount", getCount("SELECT COUNT(*) FROM pension_complaint"));
        stats.put("reservationCount", getCount("SELECT COUNT(*) FROM institution_visit_reservation"));
        stats.put("userCount", getCount("SELECT COUNT(*) FROM sys_user WHERE del_flag = '0'"));
        stats.put("testUserCount", getCount("SELECT COUNT(*) FROM sys_user WHERE del_flag = '0' AND user_id NOT IN (1) AND user_id NOT IN (SELECT user_id FROM sys_user_role WHERE role_id = 100)"));

        return AjaxResult.success(stats);
    }

    /**
     * 获取统计数量
     */
    private Integer getCount(String sql)
    {
        try
        {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        }
        catch (Exception e)
        {
            log.warn("查询统计失败: {}", sql);
            return 0;
        }
    }
}
