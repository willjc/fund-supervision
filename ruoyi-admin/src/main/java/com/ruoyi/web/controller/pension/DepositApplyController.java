package com.ruoyi.web.controller.pension;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.service.pension.IDepositApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 押金使用申请Controller
 *
 * @author ruoyi
 * @date 2025-11-13
 */
@RestController
@RequestMapping("/pension/deposit/apply")
public class DepositApplyController extends BaseController
{
    @Autowired
    private IDepositApplyService depositApplyService;

    /**
     * 查询押金使用申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:list')")
    @GetMapping("/list")
    public TableDataInfo list(DepositApply depositApply)
    {
        startPage();
        // 数据权限过滤: 只查询当前用户关联的机构的押金申请
        depositApply.setCurrentUserId(getUserId());
        List<DepositApply> list = depositApplyService.selectDepositApplyList(depositApply);
        return getDataTable(list);
    }

    /**
     * 根据老人ID查询押金使用申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:list')")
    @GetMapping("/elder/{elderId}")
    public AjaxResult listByElder(@PathVariable("elderId") Long elderId)
    {
        List<DepositApply> list = depositApplyService.selectDepositApplyByElderId(elderId);
        return AjaxResult.success(list);
    }

    /**
     * 根据机构ID查询押金使用申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:list')")
    @GetMapping("/institution/{institutionId}")
    public AjaxResult listByInstitution(@PathVariable("institutionId") Long institutionId)
    {
        List<DepositApply> list = depositApplyService.selectDepositApplyByInstitutionId(institutionId);
        return AjaxResult.success(list);
    }

    /**
     * 导出押金使用申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:export')")
    @Log(title = "押金使用申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DepositApply depositApply)
    {
        List<DepositApply> list = depositApplyService.selectDepositApplyList(depositApply);
        ExcelUtil<DepositApply> util = new ExcelUtil<DepositApply>(DepositApply.class);
        util.exportExcel(response, list, "押金使用申请数据");
    }

    /**
     * 获取押金使用申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping(value = "/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId)
    {
        return AjaxResult.success(depositApplyService.selectDepositApplyByApplyId(applyId));
    }

    /**
     * 检查老人是否有正在审批中的申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping(value = "/checkPending/{elderId}")
    public AjaxResult checkPendingApply(@PathVariable("elderId") Long elderId)
    {
        // 查询该老人的所有申请
        DepositApply query = new DepositApply();
        query.setElderId(elderId);
        List<DepositApply> list = depositApplyService.selectDepositApplyList(query);

        // 检查是否有正在审批中的申请（pending_family、family_approved、pending_supervision）
        for (DepositApply apply : list) {
            String status = apply.getApplyStatus();
            if ("pending_family".equals(status) ||
                "family_approved".equals(status) ||
                "pending_supervision".equals(status)) {
                // 找到正在审批中的申请，返回该申请的信息
                return AjaxResult.success(apply);
            }
        }

        // 没有正在审批中的申请
        return AjaxResult.success(null);
    }

    /**
     * 新增押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:add')")
    @Log(title = "押金使用申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DepositApply depositApply)
    {
        depositApply.setCreateBy(SecurityUtils.getUsername());
        // 新增时默认状态为草稿
        if (depositApply.getApplyStatus() == null) {
            depositApply.setApplyStatus("draft");
        }
        // 生成申请单号
        if (depositApply.getApplyNo() == null) {
            depositApply.setApplyNo("DEP" + System.currentTimeMillis());
        }
        return toAjax(depositApplyService.insertDepositApply(depositApply));
    }

    /**
     * 修改押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:edit')")
    @Log(title = "押金使用申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DepositApply depositApply)
    {
        // 只有草稿和已撤回状态才能修改
        DepositApply existApply = depositApplyService.selectDepositApplyByApplyId(depositApply.getApplyId());
        if (existApply == null) {
            return AjaxResult.error("押金使用申请不存在");
        }
        String status = existApply.getApplyStatus();
        if (!"draft".equals(status) && !"withdrawn".equals(status)) {
            return AjaxResult.error("当前状态不允许修改");
        }

        depositApply.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(depositApplyService.updateDepositApply(depositApply));
    }

    /**
     * 提交押金使用申请(从草稿提交到待家属审批)
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:edit')")
    @Log(title = "提交押金使用申请", businessType = BusinessType.UPDATE)
    @PutMapping("/submit/{applyId}")
    public AjaxResult submit(@PathVariable("applyId") Long applyId)
    {
        DepositApply apply = depositApplyService.selectDepositApplyByApplyId(applyId);
        if (apply == null) {
            return AjaxResult.error("押金使用申请不存在");
        }

        // 只有草稿和已撤回状态才能提交
        String status = apply.getApplyStatus();
        if (!"draft".equals(status) && !"withdrawn".equals(status)) {
            return AjaxResult.error("当前状态不允许提交");
        }

        DepositApply updateApply = new DepositApply();
        updateApply.setApplyId(applyId);
        updateApply.setApplyStatus("pending_family");
        updateApply.setUpdateBy(SecurityUtils.getUsername());

        return toAjax(depositApplyService.updateDepositApply(updateApply));
    }

    /**
     * 家属审批押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:approve')")
    @Log(title = "家属审批押金使用", businessType = BusinessType.UPDATE)
    @PutMapping("/familyApprove/{applyId}")
    public AjaxResult familyApprove(@PathVariable("applyId") Long applyId, @RequestBody DepositApply depositApply)
    {
        String opinion = depositApply.getFamilyApproveOpinion();
        String approver = SecurityUtils.getUsername();

        // 解析审批意见：判断是同意还是拒绝
        // 如果opinion包含"同意"或等于"approved"，则视为同意；否则视为拒绝，opinion内容作为拒绝原因
        String statusFlag;
        String rejectReason;
        if ("approved".equals(opinion) || (opinion != null && opinion.contains("同意"))) {
            statusFlag = "approved";
            rejectReason = "";
        } else {
            statusFlag = "rejected";
            rejectReason = (opinion != null && !opinion.trim().isEmpty()) ? opinion : "拒绝";
        }

        try {
            int result = depositApplyService.familyApprove(applyId, statusFlag, approver, rejectReason);
            return toAjax(result);
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 监管部门审批押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:approve')")
    @Log(title = "监管部门审批押金使用", businessType = BusinessType.UPDATE)
    @PutMapping("/supervisionApprove/{applyId}")
    public AjaxResult supervisionApprove(@PathVariable("applyId") Long applyId, @RequestBody DepositApply depositApply)
    {
        boolean approved = "approved".equals(depositApply.getApplyStatus());
        String remark = depositApply.getApproveRemark();
        String approver = SecurityUtils.getUsername();

        try {
            int result = depositApplyService.supervisionApprove(applyId, approved, remark, approver);
            return toAjax(result);
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 撤回押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:withdraw')")
    @Log(title = "撤回押金使用申请", businessType = BusinessType.UPDATE)
    @PutMapping("/withdraw/{applyId}")
    public AjaxResult withdraw(@PathVariable("applyId") Long applyId)
    {
        try {
            int result = depositApplyService.withdrawApply(applyId);
            return toAjax(result);
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:remove')")
    @Log(title = "押金使用申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds)
    {
        return toAjax(depositApplyService.deleteDepositApplyByApplyIds(applyIds));
    }
}
