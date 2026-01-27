package com.ruoyi.web.controller.pension;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.domain.pension.FundTransferApply;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.service.pension.IFundTransferApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 资金划拨申请Controller
 *
 * @author ruoyi
 * @date 2026-01-28
 */
@RestController
@RequestMapping("/pension/fundTransferApply")
public class FundTransferApplyController extends BaseController
{
    @Autowired
    private IFundTransferApplyService fundTransferApplyService;

    /**
     * 查询资金划拨申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(FundTransferApply fundTransferApply)
    {
        startPage();
        // 数据权限过滤: 只对机构用户过滤，监管用户可以看到所有机构的申请
        // 判断是否为监管用户（通过检查是否有监管相关权限）
        if (!isSupervisionUser()) {
            fundTransferApply.setCurrentUserId(getUserId());
        }
        List<FundTransferApply> list = fundTransferApplyService.selectFundTransferApplyList(fundTransferApply);
        return getDataTable(list);
    }

    /**
     * 判断当前用户是否为监管用户
     * 监管用户具有监管审批权限，可以查看所有机构的划拨申请
     */
    private boolean isSupervisionUser()
    {
        try {
            // 检查当前用户是否为管理员或是否有监管审批权限
            return com.ruoyi.common.utils.SecurityUtils.isAdmin(com.ruoyi.common.utils.SecurityUtils.getUserId())
                || com.ruoyi.common.utils.SecurityUtils.hasPermi("pension:fundTransferApply:supervisionApprove");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据老人ID查询资金划拨申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:list')")
    @GetMapping("/elder/{elderId}")
    public AjaxResult listByElder(@PathVariable("elderId") Long elderId)
    {
        List<FundTransferApply> list = fundTransferApplyService.selectFundTransferApplyByElderId(elderId);
        return AjaxResult.success(list);
    }

    /**
     * 根据机构ID查询资金划拨申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:list')")
    @GetMapping("/institution/{institutionId}")
    public AjaxResult listByInstitution(@PathVariable("institutionId") Long institutionId)
    {
        List<FundTransferApply> list = fundTransferApplyService.selectFundTransferApplyByInstitutionId(institutionId);
        return AjaxResult.success(list);
    }

    /**
     * 获取老人的待划拨划拨单列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:list')")
    @GetMapping("/pendingTransfer/{elderId}")
    public AjaxResult getPendingTransfer(@PathVariable("elderId") Long elderId)
    {
        List<FundTransfer> list = fundTransferApplyService.selectPendingTransferByElderId(elderId);
        return AjaxResult.success(list);
    }

    /**
     * 导出资金划拨申请列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:export')")
    @Log(title = "资金划拨申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FundTransferApply fundTransferApply)
    {
        List<FundTransferApply> list = fundTransferApplyService.selectFundTransferApplyList(fundTransferApply);
        ExcelUtil<FundTransferApply> util = new ExcelUtil<FundTransferApply>(FundTransferApply.class);
        util.exportExcel(response, list, "资金划拨申请数据");
    }

    /**
     * 获取资金划拨申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:query')")
    @GetMapping(value = "/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId)
    {
        return AjaxResult.success(fundTransferApplyService.selectFundTransferApplyByApplyId(applyId));
    }

    /**
     * 新增资金划拨申请
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:add')")
    @Log(title = "资金划拨申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> params)
    {
        FundTransferApply apply = new FundTransferApply();
        apply.setInstitutionId(getLong(params, "institutionId"));
        apply.setElderId(getLong(params, "elderId"));
        apply.setApplyReason(getString(params, "applyReason"));
        apply.setUrgencyLevel(getString(params, "urgencyLevel"));
        apply.setExpectedUseDate(getDate(params, "expectedUseDate"));
        apply.setAttachments(getString(params, "attachments"));
        apply.setRemark(getString(params, "remark"));
        apply.setCreateBy(getUsername());

        // 处理transferIds，支持Integer和Long类型
        List<Long> transferIds = getLongList(params, "transferIds");

        try {
            int result = fundTransferApplyService.createFundTransferApply(apply, transferIds);
            return toAjax(result);
        } catch (Exception e) {
            return AjaxResult.error("创建申请失败：" + e.getMessage());
        }
    }

    /**
     * 修改资金划拨申请
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:edit')")
    @Log(title = "资金划拨申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FundTransferApply fundTransferApply)
    {
        fundTransferApply.setUpdateBy(getUsername());
        return toAjax(fundTransferApplyService.updateFundTransferApply(fundTransferApply));
    }

    /**
     * 删除资金划拨申请
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:remove')")
    @Log(title = "资金划拨申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds)
    {
        return toAjax(fundTransferApplyService.deleteFundTransferApplyByApplyIds(applyIds));
    }

    /**
     * 家属审批资金划拨申请
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:familyApprove')")
    @Log(title = "家属审批划拨申请", businessType = BusinessType.UPDATE)
    @PutMapping("/familyApprove")
    public AjaxResult familyApprove(@RequestBody Map<String, Object> params)
    {
        try {
            Long applyId = getLong(params, "applyId");
            boolean approved = getBoolean(params, "approved");
            String opinion = getString(params, "opinion");
            String approverName = getString(params, "approverName");
            String relation = getString(params, "relation");
            String phone = getString(params, "phone");

            int result = fundTransferApplyService.familyApprove(applyId, approved, opinion, approverName, relation, phone);
            return toAjax(result);
        } catch (Exception e) {
            return AjaxResult.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 监管部门审批资金划拨申请
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:supervisionApprove')")
    @Log(title = "监管审批划拨申请", businessType = BusinessType.UPDATE)
    @PutMapping("/supervisionApprove")
    public AjaxResult supervisionApprove(@RequestBody Map<String, Object> params)
    {
        try {
            Long applyId = getLong(params, "applyId");
            boolean approved = getBoolean(params, "approved");
            String remark = getString(params, "remark");
            String approver = getUsername();

            int result = fundTransferApplyService.supervisionApprove(applyId, approved, remark, approver);
            return toAjax(result);
        } catch (Exception e) {
            return AjaxResult.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 执行划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransferApply:execute')")
    @Log(title = "执行划拨", businessType = BusinessType.UPDATE)
    @PutMapping("/execute/{applyId}")
    public AjaxResult execute(@PathVariable Long applyId)
    {
        try {
            int result = fundTransferApplyService.executeTransfer(applyId);
            return toAjax(result);
        } catch (Exception e) {
            return AjaxResult.error("执行划拨失败：" + e.getMessage());
        }
    }

    // 辅助方法
    private Long getLong(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) return Long.parseLong((String) value);
        return null;
    }

    private List<Long> getLongList(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return null;

        List<Long> result = new java.util.ArrayList<>();
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            for (Object item : list) {
                if (item instanceof Long) {
                    result.add((Long) item);
                } else if (item instanceof Integer) {
                    result.add(((Integer) item).longValue());
                } else if (item instanceof String) {
                    result.add(Long.parseLong((String) item));
                }
            }
        }
        return result;
    }

    private String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : null;
    }

    private Boolean getBoolean(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }

    private java.util.Date getDate(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return null;
        if (value instanceof java.util.Date) return (java.util.Date) value;
        if (value instanceof String) {
            String dateStr = (String) value;
            try {
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
