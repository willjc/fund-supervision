package com.ruoyi.web.controller.pension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.RefundRecord;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.IRefundRecordService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 民政监管-退款审批Controller
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/pension/supervision/refund")
public class SupervisionRefundController extends BaseController
{
    @Autowired
    private IRefundRecordService refundRecordService;

    @Autowired
    private IAccountInfoService accountInfoService;

    @Autowired
    private IExpenseRecordService expenseRecordService;

    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    /**
     * 查询退款审批列表
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:list')")
    @GetMapping("/approval/list")
    public TableDataInfo list(RefundRecord refundRecord)
    {
        startPage();
        // 数据权限过滤: admin超级管理员可以看到所有退款记录，其他用户只能看到关联机构的退款记录
        if (!getUserId().equals(1L)) {
            refundRecord.setCurrentUserId(getUserId());
        }
        // 支持按状态筛选，前端传递refundStatus参数
        List<RefundRecord> list = refundRecordService.selectRefundRecordList(refundRecord);
        return getDataTable(list);
    }

    /**
     * 查询所有状态的退款记录
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:list')")
    @GetMapping("/approval/all")
    public TableDataInfo listAll(RefundRecord refundRecord)
    {
        startPage();
        // 数据权限过滤: admin超级管理员可以看到所有退款记录，其他用户只能看到关联机构的退款记录
        if (!getUserId().equals(1L)) {
            refundRecord.setCurrentUserId(getUserId());
        }
        List<RefundRecord> list = refundRecordService.selectRefundRecordList(refundRecord);
        return getDataTable(list);
    }

    /**
     * 导出退款审批列表
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:export')")
    @Log(title = "退款审批", businessType = BusinessType.EXPORT)
    @PostMapping("/approval/export")
    public void export(HttpServletResponse response, RefundRecord refundRecord)
    {
        List<RefundRecord> list = refundRecordService.selectRefundRecordList(refundRecord);
        ExcelUtil<RefundRecord> util = new ExcelUtil<RefundRecord>(RefundRecord.class);
        util.exportExcel(response, list, "退款审批数据");
    }

    /**
     * 获取退款详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:query')")
    @GetMapping(value = "/approval/{refundId}")
    public AjaxResult getInfo(@PathVariable("refundId") Long refundId)
    {
        return AjaxResult.success(refundRecordService.selectRefundRecordByRefundId(refundId));
    }

    /**
     * 审批通过退款申请
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:approve')")
    @Log(title = "退款审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/approve/{refundId}")
    public AjaxResult approve(@PathVariable Long refundId)
    {
        RefundRecord refundRecord = refundRecordService.selectRefundRecordByRefundId(refundId);
        if (refundRecord == null) {
            return AjaxResult.error("退款记录不存在");
        }

        if (!"0".equals(refundRecord.getRefundStatus())) {
            return AjaxResult.error("只能审批待处理状态的退款");
        }

        // 获取老人账户信息
        Long elderId = refundRecord.getElderId();
        Long institutionId = refundRecord.getInstitutionId();

        // 查询老人账户（通��elderId查询）
        AccountInfo queryAccount = new AccountInfo();
        queryAccount.setElderId(elderId);
        queryAccount.setInstitutionId(institutionId);
        List<AccountInfo> accountList = accountInfoService.selectAccountInfoList(queryAccount);
        if (accountList == null || accountList.isEmpty()) {
            return AjaxResult.error("未找到对应的老人账户信息");
        }
        AccountInfo account = accountList.get(0);

        // 获取退款金额
        BigDecimal serviceRefundAmount = refundRecord.getServiceRefundAmount() != null ?
            refundRecord.getServiceRefundAmount() : BigDecimal.ZERO;
        BigDecimal depositRefundAmount = refundRecord.getDepositRefundAmount() != null ?
            refundRecord.getDepositRefundAmount() : BigDecimal.ZERO;
        BigDecimal memberRefundAmount = refundRecord.getMemberRefundAmount() != null ?
            refundRecord.getMemberRefundAmount() : BigDecimal.ZERO;
        BigDecimal totalRefundAmount = refundRecord.getRefundAmount();

        // 获取当前余额
        BigDecimal currentServiceBalance = account.getServiceBalance() != null ?
            account.getServiceBalance() : BigDecimal.ZERO;
        BigDecimal currentDepositBalance = account.getDepositBalance() != null ?
            account.getDepositBalance() : BigDecimal.ZERO;
        BigDecimal currentMemberBalance = account.getMemberBalance() != null ?
            account.getMemberBalance() : BigDecimal.ZERO;
        BigDecimal currentTotalBalance = account.getTotalBalance() != null ?
            account.getTotalBalance() : BigDecimal.ZERO;

        // 验证余额是否足够
        if (currentServiceBalance.compareTo(serviceRefundAmount) < 0) {
            return AjaxResult.error("服务费余额不足，当前余额：" + currentServiceBalance + "元");
        }
        if (currentDepositBalance.compareTo(depositRefundAmount) < 0) {
            return AjaxResult.error("押金余额不足，当前余额：" + currentDepositBalance + "元");
        }
        if (currentMemberBalance.compareTo(memberRefundAmount) < 0) {
            return AjaxResult.error("会员费余额不足，当前余额：" + currentMemberBalance + "元");
        }

        // 扣减账户余额
        BigDecimal newServiceBalance = currentServiceBalance.subtract(serviceRefundAmount);
        BigDecimal newDepositBalance = currentDepositBalance.subtract(depositRefundAmount);
        BigDecimal newMemberBalance = currentMemberBalance.subtract(memberRefundAmount);
        BigDecimal newTotalBalance = currentTotalBalance.subtract(totalRefundAmount);

        account.setServiceBalance(newServiceBalance);
        account.setDepositBalance(newDepositBalance);
        account.setMemberBalance(newMemberBalance);
        account.setTotalBalance(newTotalBalance);
        account.setUpdateTime(DateUtils.getNowDate());

        // 更新账户余额
        int accountUpdateResult = accountInfoService.updateAccountInfo(account);
        if (accountUpdateResult <= 0) {
            return AjaxResult.error("更新账户余额失败");
        }

        // 记录余额变动日志（分别记录服务费、押金、会员费退款）
        try {
            if (serviceRefundAmount.compareTo(BigDecimal.ZERO) > 0) {
                expenseRecordService.createExpenseRecord(
                    elderId, account.getAccountId(), "service", "expense",
                    serviceRefundAmount, "服务费退款-" + refundRecord.getRefundNo(),
                    refundId, "refund", currentTotalBalance, newTotalBalance
                );
            }
            if (depositRefundAmount.compareTo(BigDecimal.ZERO) > 0) {
                expenseRecordService.createExpenseRecord(
                    elderId, account.getAccountId(), "deposit", "expense",
                    depositRefundAmount, "押金退款-" + refundRecord.getRefundNo(),
                    refundId, "refund", currentTotalBalance, newTotalBalance
                );
            }
            if (memberRefundAmount.compareTo(BigDecimal.ZERO) > 0) {
                expenseRecordService.createExpenseRecord(
                    elderId, account.getAccountId(), "member", "expense",
                    memberRefundAmount, "会员费退款-" + refundRecord.getRefundNo(),
                    refundId, "refund", currentTotalBalance, newTotalBalance
                );
            }
        } catch (Exception e) {
            System.err.println("记录余额变动日志异常：" + e.getMessage());
        }

        // 记录监管账户流水（退款划拨到基本账户）
        try {
            supervisionAccountLogService.recordTransferOut(
                institutionId,
                refundId,
                totalRefundAmount,
                "退款划拨-" + refundRecord.getRefundNo(),
                "基本账户"
            );
        } catch (Exception e) {
            System.err.println("记录监管账户流水异常：" + e.getMessage());
        }

        // 更新退款状态为成功
        refundRecord.setRefundStatus("1");
        refundRecord.setRefundTime(new Date());
        refundRecord.setApprover(getUsername());
        refundRecord.setApproveTime(new Date());

        return toAjax(refundRecordService.updateRefundRecord(refundRecord));
    }

    /**
     * 审批拒绝退款申请
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:approve')")
    @Log(title = "退款审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/reject/{refundId}")
    public AjaxResult reject(@PathVariable Long refundId, @RequestBody RefundRecord refundRecord)
    {
        RefundRecord existing = refundRecordService.selectRefundRecordByRefundId(refundId);
        if (existing == null) {
            return AjaxResult.error("退款记录不存在");
        }

        if (!"0".equals(existing.getRefundStatus())) {
            return AjaxResult.error("只能审批待处理状态的退款");
        }

        // 更新状态为失败
        existing.setRefundStatus("2");
        existing.setApprover(getUsername());
        existing.setApproveTime(new java.util.Date());
        existing.setRemark(refundRecord.getRemark()); // 拒绝原因

        return toAjax(refundRecordService.updateRefundRecord(existing));
    }

    /**
     * 获取审批统计信息
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:query')")
    @GetMapping("/approval/statistics")
    public AjaxResult getApprovalStatistics()
    {
        RefundRecord query = new RefundRecord();

        // 待处理数量
        query.setRefundStatus("0");
        int pendingCount = refundRecordService.selectRefundRecordList(query).size();

        // 成功数量
        query.setRefundStatus("1");
        int approvedCount = refundRecordService.selectRefundRecordList(query).size();

        // 失败数量
        query.setRefundStatus("2");
        int rejectedCount = refundRecordService.selectRefundRecordList(query).size();

        // 构建统计结果
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("pendingCount", pendingCount);
        statistics.put("approvedCount", approvedCount);
        statistics.put("rejectedCount", rejectedCount);
        statistics.put("totalCount", pendingCount + approvedCount + rejectedCount);

        return AjaxResult.success(statistics);
    }

    /**
     * 批量审批
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:approve')")
    @Log(title = "退款审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/batchApprove")
    public AjaxResult batchApprove(@RequestBody Long[] refundIds)
    {
        int successCount = 0;
        int failCount = 0;

        for (Long refundId : refundIds) {
            try {
                RefundRecord refundRecord = refundRecordService.selectRefundRecordByRefundId(refundId);
                if (refundRecord != null && "0".equals(refundRecord.getRefundStatus())) {
                    refundRecord.setRefundStatus("1");
                    refundRecord.setApprover(getUsername());
                    refundRecord.setApproveTime(new java.util.Date());
                    refundRecordService.updateRefundRecord(refundRecord);
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        String message = String.format("批量审批完成：成功 %d 个，失败 %d 个", successCount, failCount);
        return AjaxResult.success(message);
    }

    /**
     * 获取今日待处理退款
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:query')")
    @GetMapping("/approval/todayPending")
    public AjaxResult getTodayPending()
    {
        RefundRecord query = new RefundRecord();
        query.setRefundStatus("0");
        List<RefundRecord> allPending = refundRecordService.selectRefundRecordList(query);

        // 筛选今日的退款记录
        List<RefundRecord> todayPending = allPending.stream()
            .filter(refund -> {
                java.util.Date createTime = refund.getCreateTime();
                if (createTime != null) {
                    java.time.LocalDate createLocalDate = createTime.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                    return createLocalDate.equals(java.time.LocalDate.now());
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());

        return AjaxResult.success(todayPending);
    }

    /**
     * 获取大额退款（金额较大的）
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:query')")
    @GetMapping("/approval/largeAmount")
    public AjaxResult getLargeAmountRefunds()
    {
        RefundRecord query = new RefundRecord();
        query.setRefundStatus("0");
        List<RefundRecord> allPending = refundRecordService.selectRefundRecordList(query);

        // 筛选金额大于10000的退款记录
        List<RefundRecord> largeAmountRefunds = allPending.stream()
            .filter(refund -> {
                if (refund.getRefundAmount() != null) {
                    return refund.getRefundAmount().doubleValue() > 10000.0;
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());

        return AjaxResult.success(largeAmountRefunds);
    }

    /**
     * 按退款原因统计
     */
    @PreAuthorize("@ss.hasPermi('pension:refund:query')")
    @GetMapping("/approval/statisticsByReason")
    public AjaxResult getStatisticsByReason()
    {
        RefundRecord query = new RefundRecord();
        query.setRefundStatus("0");
        List<RefundRecord> allPending = refundRecordService.selectRefundRecordList(query);

        // 按退款原因分组统计
        java.util.Map<String, Long> reasonStatistics = allPending.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                refund -> refund.getRefundReason() != null ? refund.getRefundReason() : "其他",
                java.util.stream.Collectors.counting()
            ));

        return AjaxResult.success(reasonStatistics);
    }
}