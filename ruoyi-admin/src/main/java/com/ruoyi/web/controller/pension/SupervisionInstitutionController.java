package com.ruoyi.web.controller.pension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询养老机构入驻申请列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:list')")
    @GetMapping("/approval/list")
    public TableDataInfo list(PensionInstitution pensionInstitution)
    {
        startPage();
        // 处理日期范围参数：从request中获取beginTime和endTime，放入params Map
        // 前端通过queryParams.beginTime和queryParams.endTime传递，但Mapper期望params.beginTime和params.endTime
        // 这里需要手动将参数迁移到params中
        // 注意：由于Spring会自动绑定beginTime和endTime字段到实体类（如果存在），
        // 我们需要通过其他方式来处理

        // 不再强制设置status,由前端传递的参数决定
        // 如果前端没有传递status参数,则查询所有状态
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);

        // 填充计算字段（开户状态、资金统计、床位数据）
        for (PensionInstitution institution : list) {
            Long institutionId = institution.getInstitutionId();

            // 判断是否已开户
            boolean hasSupervisionAccount = institution.getSuperviseAccount() != null
                    && !institution.getSuperviseAccount().trim().isEmpty()
                    && institution.getBankAccount() != null
                    && !institution.getBankAccount().trim().isEmpty();
            institution.setHasSupervisionAccount(hasSupervisionAccount);

            // 查询资金统计
            try {
                String fundSql = "SELECT COALESCE(SUM(service_balance), 0) as service_balance, " +
                                 "COALESCE(SUM(deposit_balance), 0) as deposit_balance, " +
                                 "COALESCE(SUM(member_balance), 0) as member_balance " +
                                 "FROM account_info WHERE institution_id = ?";
                Map<String, Object> fundData = jdbcTemplate.queryForMap(fundSql, institutionId);
                institution.setServiceFeeBalance((BigDecimal) fundData.get("service_balance"));
                institution.setDepositBalance((BigDecimal) fundData.get("deposit_balance"));
                institution.setMemberFeeBalance((BigDecimal) fundData.get("member_balance"));
            } catch (Exception e) {
                // 查询失败时设置为0
                institution.setServiceFeeBalance(BigDecimal.ZERO);
                institution.setDepositBalance(BigDecimal.ZERO);
                institution.setMemberFeeBalance(BigDecimal.ZERO);
            }

            // 查询床位数据（从bed_info表统计）
            try {
                String bedSql = "SELECT COUNT(*) as total_beds, " +
                                "SUM(CASE WHEN bed_status = '1' THEN 1 ELSE 0 END) as used_beds " +
                                "FROM bed_info WHERE institution_id = ?";
                Map<String, Object> bedData = jdbcTemplate.queryForMap(bedSql, institutionId);
                Integer totalBeds = ((Number) bedData.get("total_beds")).intValue();
                Integer usedBeds = ((Number) bedData.get("used_beds")).intValue();
                // 如果bed_info有数据，使用实际统计；否则使用pension_institution的bed_count
                if (totalBeds > 0) {
                    institution.setBedCount(totalBeds.longValue());
                    institution.setActualElders(usedBeds);
                } else {
                    // bed_info无数据时，查询elder_check_in获取入住老人数
                    try {
                        String elderSql = "SELECT COUNT(*) FROM elder_check_in WHERE institution_id = ?";
                        Integer actualElders = jdbcTemplate.queryForObject(elderSql, Integer.class, institutionId);
                        institution.setActualElders(actualElders != null ? actualElders : 0);
                    } catch (Exception ex) {
                        institution.setActualElders(0);
                    }
                }
            } catch (Exception e) {
                // 查询失败时，尝试从elder_check_in统计
                try {
                    String elderSql = "SELECT COUNT(*) FROM elder_check_in WHERE institution_id = ?";
                    Integer actualElders = jdbcTemplate.queryForObject(elderSql, Integer.class, institutionId);
                    institution.setActualElders(actualElders != null ? actualElders : 0);
                } catch (Exception ex) {
                    institution.setActualElders(0);
                }
            }
        }

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
    public AjaxResult getApprovalStatistics(PensionInstitution pensionInstitution)
    {
        // 待审批数量
        PensionInstitution query = new PensionInstitution();
        copyCommonQueryParams(pensionInstitution, query);
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
     * 复制通用查询参数（不包括status）
     */
    private void copyCommonQueryParams(PensionInstitution source, PensionInstitution target) {
        if (source.getParams() != null) {
            if (target.getParams() == null) {
                target.setParams(new java.util.HashMap<>());
            }
            if (source.getParams().get("beginTime") != null) {
                target.getParams().put("beginTime", source.getParams().get("beginTime"));
            }
            if (source.getParams().get("endTime") != null) {
                target.getParams().put("endTime", source.getParams().get("endTime"));
            }
        }
        target.setInstitutionName(source.getInstitutionName());
        target.setCreditCode(source.getCreditCode());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setInstitutionType(source.getInstitutionType());
        target.setDistrictCode(source.getDistrictCode());
        target.setAreaCode(source.getAreaCode());
        target.setCareLevels(source.getCareLevels());
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

        // 判断是否已开户：监管账户和基本账户都有值即为已开户
        boolean hasSupervisionAccount = institution.getSuperviseAccount() != null
                && !institution.getSuperviseAccount().trim().isEmpty()
                && institution.getBankAccount() != null
                && !institution.getBankAccount().trim().isEmpty();

        // 查询资金统计数据
        BigDecimal serviceFeeBalance = BigDecimal.ZERO;
        BigDecimal depositBalance = BigDecimal.ZERO;
        BigDecimal memberFeeBalance = BigDecimal.ZERO;
        try {
            String fundSql = "SELECT COALESCE(SUM(service_balance), 0) as service_balance, " +
                             "COALESCE(SUM(deposit_balance), 0) as deposit_balance, " +
                             "COALESCE(SUM(member_balance), 0) as member_balance " +
                             "FROM account_info WHERE institution_id = ?";
            Map<String, Object> fundData = jdbcTemplate.queryForMap(fundSql, institutionId);
            serviceFeeBalance = (BigDecimal) fundData.get("service_balance");
            depositBalance = (BigDecimal) fundData.get("deposit_balance");
            memberFeeBalance = (BigDecimal) fundData.get("member_balance");
        } catch (Exception e) {
            logger.warn("查询机构资金统计失败: institutionId={}", institutionId, e);
        }

        // 查询床位数据（从bed_info表统计）
        Integer approvedBeds = institution.getBedCount() != null ? institution.getBedCount().intValue() : 0;
        Integer actualElders = 0;
        try {
            String bedSql = "SELECT COUNT(*) as total_beds, " +
                            "SUM(CASE WHEN bed_status = '1' THEN 1 ELSE 0 END) as used_beds " +
                            "FROM bed_info WHERE institution_id = ?";
            Map<String, Object> bedData = jdbcTemplate.queryForMap(bedSql, institutionId);
            Integer totalBeds = ((Number) bedData.get("total_beds")).intValue();
            Integer usedBeds = ((Number) bedData.get("used_beds")).intValue();
            // 如果bed_info有数据，使用实际统计
            if (totalBeds > 0) {
                approvedBeds = totalBeds;
                actualElders = usedBeds;
            } else {
                // bed_info无数据时，查询elder_check_in获取入住老人数
                try {
                    String elderSql = "SELECT COUNT(*) FROM elder_check_in WHERE institution_id = ?";
                    actualElders = jdbcTemplate.queryForObject(elderSql, Integer.class, institutionId);
                } catch (Exception ex) {
                    logger.warn("查询机构老人数失败: institutionId={}", institutionId, ex);
                }
            }
        } catch (Exception e) {
            // 查询失败时，尝试从elder_check_in统计
            try {
                String elderSql = "SELECT COUNT(*) FROM elder_check_in WHERE institution_id = ?";
                actualElders = jdbcTemplate.queryForObject(elderSql, Integer.class, institutionId);
            } catch (Exception ex) {
                logger.warn("查询机构老人数失败: institutionId={}", institutionId, ex);
            }
        }

        // 构建返回数据
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("institutionId", institution.getInstitutionId());
        data.put("institutionName", institution.getInstitutionName());
        data.put("creditCode", institution.getCreditCode());
        data.put("legalPerson", institution.getLegalPerson());
        data.put("contactPerson", institution.getContactPerson());
        data.put("contactPhone", institution.getContactPhone());
        data.put("contactEmail", institution.getContactEmail());
        data.put("registeredCapital", institution.getRegisteredCapital());
        data.put("registerAddress", institution.getRegisteredAddress());
        data.put("actualAddress", institution.getActualAddress());
        data.put("status", institution.getStatus());
        data.put("registerTime", institution.getCreateTime());
        data.put("establishedDate", institution.getEstablishedDate());
        data.put("bedCount", approvedBeds);
        data.put("actualElders", actualElders);
        data.put("rating", institution.getRatingLevel() != null ? institution.getRatingLevel() : 3);
        data.put("serviceFeeBalance", serviceFeeBalance);
        data.put("depositBalance", depositBalance);
        data.put("memberFeeBalance", memberFeeBalance);
        data.put("hasSupervisionAccount", hasSupervisionAccount);
        data.put("supervisionBank", hasSupervisionAccount ? institution.getSuperviseAccount() : null);
        data.put("supervisionAccount", institution.getSuperviseAccount());
        data.put("bankAccount", institution.getBankAccount());

        // 公示信息字段
        data.put("recordNumber", institution.getRecordNumber());
        data.put("organizer", institution.getOrganizer());
        data.put("fixedAssets", institution.getFixedAssets());
        data.put("responsibleName", institution.getResponsibleName());
        data.put("responsiblePhone", institution.getResponsiblePhone());
        data.put("responsibleIdCard", institution.getResponsibleIdCard());
        data.put("responsibleAddress", institution.getResponsibleAddress());
        data.put("businessScope", institution.getBusinessScope());
        data.put("feeRange", institution.getFeeRange());
        data.put("priceRangeMin", institution.getPriceRangeMin());
        data.put("priceRangeMax", institution.getPriceRangeMax());
        data.put("nursingFeeMin", institution.getNursingFeeMin());
        data.put("nursingFeeMax", institution.getNursingFeeMax());
        data.put("bedFeeMin", institution.getBedFeeMin());
        data.put("bedFeeMax", institution.getBedFeeMax());
        data.put("mealFeeMin", institution.getMealFeeMin());
        data.put("mealFeeMax", institution.getMealFeeMax());
        data.put("careLevels", institution.getCareLevels());
        data.put("medicalCondition", institution.getMedicalCondition());
        data.put("freeTrial", institution.getFreeTrial());
        data.put("remark", institution.getRemark());

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