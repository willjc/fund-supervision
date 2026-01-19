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
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 民政监管-机构入驻审批Controller
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/pension/supervision/institution")
public class SupervisionInstitutionController extends BaseController
{
    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    /**
     * 查询养老机构入驻申请列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:list')")
    @GetMapping("/approval/list")
    public TableDataInfo list(PensionInstitution pensionInstitution)
    {
        startPage();
        // 不再强制设置status,由前端传递的参数决定
        // 如果前端没有传递status参数,则查询所有状态
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        return getDataTable(list);
    }

    /**
     * 查询所有状态的机构申请（包括待审批、已入驻、已驳回）
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:list')")
    @GetMapping("/approval/all")
    public TableDataInfo listAll(PensionInstitution pensionInstitution)
    {
        startPage();
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        return getDataTable(list);
    }

    /**
     * 导出养老机构入驻申请列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:export')")
    @Log(title = "养老机构入驻申请", businessType = BusinessType.EXPORT)
    @PostMapping("/approval/export")
    public void export(HttpServletResponse response, PensionInstitution pensionInstitution)
    {
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        ExcelUtil<PensionInstitution> util = new ExcelUtil<PensionInstitution>(PensionInstitution.class);
        util.exportExcel(response, list, "养老机构入驻申请数据");
    }

    /**
     * 获取养老机构入驻申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:query')")
    @GetMapping(value = "/approval/{institutionId}")
    public AjaxResult getInfo(@PathVariable("institutionId") Long institutionId)
    {
        return AjaxResult.success(pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId));
    }

    /**
     * 审批通过养老机构入驻申请
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:approve')")
    @Log(title = "养老机构入驻申请", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/approve/{institutionId}")
    public AjaxResult approve(@PathVariable Long institutionId)
    {
        PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
        if (institution == null) {
            return AjaxResult.error("机构申请不存在");
        }

        if (!"0".equals(institution.getStatus()) && !"6".equals(institution.getStatus())) {
            return AjaxResult.error("只能审批待审批状态的申请");
        }

        boolean isMaintenance = "6".equals(institution.getStatus());

        // 根据原状态更新: 待审批(0) -> 已入驻(1), 维护待审批(6) -> 已入驻(1)
        institution.setStatus("1");
        institution.setApproveUser(getUsername());
        institution.setApproveTime(new java.util.Date());

        // 如果是维护申请，添加审批备注
        if (isMaintenance) {
            institution.setApproveRemark("维护申请审批通过，机构信息已更新");
        }

        return toAjax(pensionInstitutionService.updatePensionInstitution(institution));
    }

    /**
     * 审批拒绝养老机构入驻申请
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:reject')")
    @Log(title = "养老机构入驻申请", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/reject/{institutionId}")
    public AjaxResult reject(@PathVariable Long institutionId, @RequestBody PensionInstitution institution)
    {
        PensionInstitution existing = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
        if (existing == null) {
            return AjaxResult.error("机构申请不存在");
        }

        if (!"0".equals(existing.getStatus()) && !"6".equals(existing.getStatus())) {
            return AjaxResult.error("只能审批待审批状态的申请");
        }

        boolean isMaintenance = "6".equals(existing.getStatus());

        // 更新状态为已驳回
        existing.setStatus("2");
        existing.setApproveUser(getUsername());
        existing.setApproveTime(new java.util.Date());

        // 设置审批备注（维护申请有默认备注，否则使用拒绝原因）
        if (isMaintenance) {
            existing.setApproveRemark("维护申请审批驳回，机构信息保持不变");
        } else {
            existing.setApproveRemark(institution.getRemark()); // 拒绝原因
        }

        return toAjax(pensionInstitutionService.updatePensionInstitution(existing));
    }

    /**
     * 获取审批统计信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:query')")
    @GetMapping("/approval/statistics")
    public AjaxResult getApprovalStatistics()
    {
        PensionInstitution query = new PensionInstitution();

        // 待审批数量
        query.setStatus("0");
        int pendingCount = pensionInstitutionService.selectPensionInstitutionList(query).size();

        // 已入驻数量
        query.setStatus("1");
        int approvedCount = pensionInstitutionService.selectPensionInstitutionList(query).size();

        // 已驳回数量
        query.setStatus("2");
        int rejectedCount = pensionInstitutionService.selectPensionInstitutionList(query).size();

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
    @PreAuthorize("@ss.hasPermi('supervision:institution:approve')")
    @Log(title = "养老机构入驻申请", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/batchApprove")
    public AjaxResult batchApprove(@RequestBody Long[] institutionIds)
    {
        int successCount = 0;
        int failCount = 0;

        for (Long institutionId : institutionIds) {
            try {
                PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
                // 支持待审批(0)和维护待审批(6)状态
                if (institution != null && ("0".equals(institution.getStatus()) || "6".equals(institution.getStatus()))) {
                    boolean isMaintenance = "6".equals(institution.getStatus());
                    institution.setStatus("1");
                    institution.setApproveUser(getUsername());
                    institution.setApproveTime(new java.util.Date());
                    if (isMaintenance) {
                        institution.setApproveRemark("维护申请审批通过，机构信息已更新");
                    }
                    pensionInstitutionService.updatePensionInstitution(institution);
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
     * 查询养老机构详细信息（用于机构查询页面）
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:query')")
    @GetMapping("/approval/detail/{institutionId}")
    public AjaxResult getDetail(@PathVariable Long institutionId)
    {
        PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
        if (institution == null) {
            return AjaxResult.error("机构信息不存在");
        }

        // 模拟添加资金监管数据
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("institutionId", institution.getInstitutionId());
        data.put("institutionName", institution.getInstitutionName());
        data.put("creditCode", institution.getCreditCode());
        data.put("legalPerson", institution.getLegalPerson());
        data.put("contactPerson", institution.getContactPerson());
        data.put("contactPhone", institution.getContactPhone());
        data.put("registeredCapital", institution.getRegisteredCapital());
        data.put("registerAddress", institution.getRegisteredAddress());
        data.put("actualAddress", institution.getActualAddress());
        data.put("status", institution.getStatus());
        data.put("registerTime", new java.util.Date()); // 使用当前时间模拟
        data.put("approvedBeds", 100 + (int)(Math.random() * 200)); // 模拟床位数
        data.put("actualElders", 50 + (int)(Math.random() * 150)); // 模拟实际老人数
        data.put("rating", 4); // 模拟评级
        data.put("serviceFeeBalance", Math.random() * 1000000); // 模拟服务费余额
        data.put("depositBalance", Math.random() * 500000); // 模拟押金余额
        data.put("memberFeeBalance", Math.random() * 200000); // 模拟会员费余额
        data.put("hasSupervisionAccount", "1".equals(institution.getStatus())); // 已审批的机构默认有监管账户
        data.put("supervisionBank", "中国银行");
        data.put("supervisionAccount", "6217" + String.format("%012d", institutionId));

        return AjaxResult.success(data);
    }

    /**
     * 获取机构老人列表（模拟数据）
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:query')")
    @GetMapping("/elders/{institutionId}")
    public AjaxResult getElders(@PathVariable Long institutionId)
    {
        // 模拟老人数据
        java.util.List<java.util.Map<String, Object>> elders = new java.util.ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            java.util.Map<String, Object> elder = new java.util.HashMap<>();
            elder.put("elderName", "老人" + i);
            elder.put("idCard", "11010119" + String.format("%02d", 50 + i) + "0101" + String.format("%04d", i));
            elder.put("gender", i % 2 == 0 ? "2" : "1");
            elder.put("age", 65 + i);
            elder.put("checkInTime", new java.util.Date(System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L)));
            elder.put("bedNumber", "A" + String.format("%03d", i));
            elder.put("careLevel", i % 3 == 0 ? "三级护理" : (i % 2 == 0 ? "二级护理" : "一级护理"));
            elders.add(elder);
        }

        return AjaxResult.success(elders);
    }
}