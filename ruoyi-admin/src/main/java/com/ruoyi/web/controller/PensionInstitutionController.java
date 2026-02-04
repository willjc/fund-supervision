package com.ruoyi.web.controller;

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
import com.ruoyi.domain.ReleaseSupervision;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IReleaseSupervisionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 养老机构信息Controller
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/pension/institution")
public class PensionInstitutionController extends BaseController
{
    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    @Autowired
    private IReleaseSupervisionService releaseSupervisionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询养老机构信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:list')")
    @GetMapping("/list")
    public TableDataInfo list(PensionInstitution pensionInstitution)
    {
        startPage();
        // 养老机构端数据权限: 所有用户(包括admin)都只能查看自己关联的机构
        // 只有在民政监管端才能查看所有机构数据
        pensionInstitution.setCurrentUserId(getUserId());
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        return getDataTable(list);
    }

    /**
     * 获取所有机构列表（不分页，用于下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:list')")
    @GetMapping("/all")
    public AjaxResult listAll(PensionInstitution pensionInstitution)
    {
        // 监管端可以查看所有机构，机构端只能查看自己关联的机构
        pensionInstitution.setCurrentUserId(getUserId());
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        return AjaxResult.success(list);
    }

    /**
     * 导出养老机构信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:export')")
    @Log(title = "养老机构信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PensionInstitution pensionInstitution)
    {
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        ExcelUtil<PensionInstitution> util = new ExcelUtil<PensionInstitution>(PensionInstitution.class);
        util.exportExcel(response, list, "养老机构信息数据");
    }

    /**
     * 获取养老机构信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:query')")
    @GetMapping(value = "/{institutionId}")
    public AjaxResult getInfo(@PathVariable("institutionId") Long institutionId)
    {
        return AjaxResult.success(pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId));
    }

    /**
     * 新增养老机构信息
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:add')")
    @Log(title = "养老机构信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PensionInstitution pensionInstitution)
    {
        return toAjax(pensionInstitutionService.insertPensionInstitution(pensionInstitution));
    }

    /**
     * 修改养老机构信息
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:edit')")
    @Log(title = "养老机构信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PensionInstitution pensionInstitution)
    {
        return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
    }

    /**
     * 审批机构入驻申请
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:approve')")
    @Log(title = "审批机构入驻申请", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody PensionInstitution pensionInstitution)
    {
        pensionInstitution.setApproveUser(getUsername());
        return toAjax(pensionInstitutionService.approveInstitution(pensionInstitution));
    }

    /**
     * 提交机构入驻申请
     */
    @Log(title = "提交机构入驻申请", businessType = BusinessType.INSERT)
    @PostMapping("/apply/submit")
    public AjaxResult submitApply(@RequestBody PensionInstitution pensionInstitution)
    {
        System.out.println("==================== 提交申请接口被调用 ====================");
        System.out.println("统一信用代码: " + pensionInstitution.getCreditCode());
        System.out.println("机构名称: " + pensionInstitution.getInstitutionName());
        System.out.println("传入的institutionId: " + pensionInstitution.getInstitutionId());

        // 如果传入 institutionId，说明是编辑现有记录（草稿、已驳回等状态）
        if (pensionInstitution.getInstitutionId() != null) {
            System.out.println("编辑模式：直接更新现有记录,ID: " + pensionInstitution.getInstitutionId());
            pensionInstitution.setStatus("0"); // 设置为待审批
            pensionInstitution.setApplyTime(new java.util.Date());
            // 清除之前的审批信息，重新进入审批流程
            pensionInstitution.setApproveTime(null);
            pensionInstitution.setApproveUser(null);
            pensionInstitution.setApproveRemark(null);
            return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
        }

        // 没有 institutionId，检查是否有草稿记录（根据统一信用代码查询）
        if (pensionInstitution.getCreditCode() != null && !pensionInstitution.getCreditCode().isEmpty()) {
            PensionInstitution query = new PensionInstitution();
            query.setCreditCode(pensionInstitution.getCreditCode());
            query.setStatus("4"); // 查询草稿状态的记录
            List<PensionInstitution> existList = pensionInstitutionService.selectPensionInstitutionList(query);

            if (existList != null && !existList.isEmpty()) {
                // 更新草稿为待审批状态
                System.out.println("找到草稿,执行更新为待审批,ID: " + existList.get(0).getInstitutionId());
                PensionInstitution exist = existList.get(0);
                pensionInstitution.setInstitutionId(exist.getInstitutionId());
                pensionInstitution.setStatus("0"); // 设置为待审批
                pensionInstitution.setApplyTime(new java.util.Date());
                return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
            }
        }

        // 新增申请,直接提交审核
        System.out.println("执行新增并提交申请");
        pensionInstitution.setStatus("0");  // 0表示待审批状态
        pensionInstitution.setApplyTime(new java.util.Date());
        int result = pensionInstitutionService.insertPensionInstitution(pensionInstitution);
        System.out.println("插入结果: " + result);

        // 建立用户-机构关联(所有用户都需要建立关联以实现数据权限控制)
        if (pensionInstitution.getInstitutionId() != null) {
            createUserInstitutionRelation(pensionInstitution.getInstitutionId(), getUserId());
        }

        return toAjax(result);
    }

    /**
     * 保存机构申请草稿
     */
    @Log(title = "保存机构申请草稿", businessType = BusinessType.INSERT)
    @PostMapping("/apply/draft")
    public AjaxResult saveDraft(@RequestBody PensionInstitution pensionInstitution)
    {
        System.out.println("==================== 保存草稿接口被调用 ====================");
        System.out.println("统一信用代码: " + pensionInstitution.getCreditCode());
        System.out.println("机构名称: " + pensionInstitution.getInstitutionName());
        System.out.println("传入的institutionId: " + pensionInstitution.getInstitutionId());

        // 验证草稿必填项（数据库NOT NULL字段）
        if (pensionInstitution.getInstitutionName() == null || pensionInstitution.getInstitutionName().trim().isEmpty()) {
            return AjaxResult.error("请填写机构名称");
        }
        if (pensionInstitution.getCreditCode() == null || pensionInstitution.getCreditCode().trim().isEmpty()) {
            return AjaxResult.error("请填写统一信用代码");
        }
        if (pensionInstitution.getContactPerson() == null || pensionInstitution.getContactPerson().trim().isEmpty()) {
            return AjaxResult.error("请填写联系人");
        }
        if (pensionInstitution.getContactPhone() == null || pensionInstitution.getContactPhone().trim().isEmpty()) {
            return AjaxResult.error("请填写联系电话");
        }

        // 如果传入 institutionId，说明是编辑模式，直接执行更新
        if (pensionInstitution.getInstitutionId() != null) {
            System.out.println("编辑模式：直接更新已有记录,ID: " + pensionInstitution.getInstitutionId());
            pensionInstitution.setStatus("4"); // 设置为草稿状态
            // 清除审批相关字段,确保草稿状态的纯净性
            pensionInstitution.setApplyTime(null);
            pensionInstitution.setApproveTime(null);
            pensionInstitution.setApproveUser(null);
            pensionInstitution.setApproveRemark(null);
            return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
        }

        // 没有 institutionId，查询是否已有草稿(根据统一信用代码查询)
        if (pensionInstitution.getCreditCode() != null && !pensionInstitution.getCreditCode().isEmpty()) {
            PensionInstitution query = new PensionInstitution();
            query.setCreditCode(pensionInstitution.getCreditCode());
            query.setStatus("4"); // 只查询草稿状态的记录
            List<PensionInstitution> existList = pensionInstitutionService.selectPensionInstitutionList(query);

            if (existList != null && !existList.isEmpty()) {
                // 更新已有草稿 - 使用updateDraft方法强制清除审批相关字段
                System.out.println("找到已存在的草稿,执行更新操作,ID: " + existList.get(0).getInstitutionId());
                PensionInstitution exist = existList.get(0);
                pensionInstitution.setInstitutionId(exist.getInstitutionId());
                pensionInstitution.setStatus("4"); // 保持草稿状态
                System.out.println("设置status=4,准备调用updateDraft方法");
                return toAjax(pensionInstitutionService.updateDraft(pensionInstitution));
            }
        }

        // 新增草稿,状态设置为草稿
        System.out.println("执行新增草稿操作");
        pensionInstitution.setStatus("4");  // 4表示草稿状态
        // 清除审批相关字段,确保草稿状态的纯净性
        pensionInstitution.setApplyTime(null);
        pensionInstitution.setApproveTime(null);
        pensionInstitution.setApproveUser(null);
        pensionInstitution.setApproveRemark(null);
        System.out.println("设置status=4, applyTime=null,准备插入数据库");
        int result = pensionInstitutionService.insertPensionInstitution(pensionInstitution);
        System.out.println("插入结果: " + result);

        // 建立用户-机构关联(所有用户都需要建立关联以实现数据权限控制)
        if (pensionInstitution.getInstitutionId() != null) {
            createUserInstitutionRelation(pensionInstitution.getInstitutionId(), getUserId());
        }

        return toAjax(result);
    }

    /**
     * 提交维护申请
     */
    @Log(title = "提交维护申请", businessType = BusinessType.UPDATE)
    @PutMapping("/maintain/submit")
    public AjaxResult submitMaintain(@RequestBody PensionInstitution pensionInstitution)
    {
        // 设置为维护待审批状态
        pensionInstitution.setStatus("6");
        pensionInstitution.setApplyTime(new java.util.Date());
        return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
    }

    /**
     * 保存维护草稿(进入维护中状态)
     */
    @Log(title = "保存维护草稿", businessType = BusinessType.UPDATE)
    @PutMapping("/maintain/draft")
    public AjaxResult saveMaintainDraft(@RequestBody PensionInstitution pensionInstitution)
    {
        // 设置为维护中状态
        pensionInstitution.setStatus("5");
        return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
    }

    /**
     * 删除养老机构信息
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:remove')")
    @Log(title = "养老机构信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{institutionIds}")
    public AjaxResult remove(@PathVariable Long[] institutionIds)
    {
        return toAjax(pensionInstitutionService.deletePensionInstitutionByInstitutionIds(institutionIds));
    }

    /**
     * 建立用户-机构关联
     * @param institutionId 机构ID
     * @param userId 用户ID
     */
    private void createUserInstitutionRelation(Long institutionId, Long userId)
    {
        // 检查是否已存在关联
        String checkSql = "SELECT COUNT(*) FROM sys_user_institution WHERE user_id = ? AND institution_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, institutionId);

        if (count == null || count == 0) {
            // 不存在则创建关联
            String insertSql = "INSERT INTO sys_user_institution (user_id, institution_id) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, userId, institutionId);
            logger.info("创建用户-机构关联: userId={}, institutionId={}", userId, institutionId);
        }
    }

    /**
     * 提交解除监管申请
     */
    @Log(title = "提交解除监管申请", businessType = BusinessType.INSERT)
    @PostMapping("/release/apply")
    public AjaxResult submitReleaseApply(@RequestBody ReleaseSupervision releaseSupervision)
    {
        // 获取前端传来的机构ID
        Long institutionId = releaseSupervision.getInstitutionId();
        if (institutionId == null) {
            return AjaxResult.error("机构ID不能为空");
        }

        // 1. 验证机构是否存在
        PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
        if (institution == null) {
            return AjaxResult.error("机构不存在");
        }

        // 2. 验证机构状态，只有已入驻(status=1)的机构才能申请解除监管
        if (!"1".equals(institution.getStatus())) {
            return AjaxResult.error("只有已入驻的机构才能申请解除监管");
        }

        // 3. 验证用户是否有权操作此机构
        Long currentUserId = getUserId();
        String checkSql = "SELECT COUNT(*) FROM sys_user_institution WHERE user_id = ? AND institution_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, currentUserId, institutionId);
        if (count == null || count == 0) {
            return AjaxResult.error("无权操作此机构");
        }

        // 4. 检查是否有待审批或已批准的申请
        ReleaseSupervision checkQuery = new ReleaseSupervision();
        checkQuery.setInstitutionId(institutionId);
        List<ReleaseSupervision> existingApplies = releaseSupervisionService.selectReleaseSupervisionList(checkQuery);
        for (ReleaseSupervision existing : existingApplies) {
            if ("0".equals(existing.getApplyStatus())) {
                return AjaxResult.error("您有待审批的解除监管申请，请等待审批结果");
            }
            if ("1".equals(existing.getApplyStatus())) {
                return AjaxResult.error("您的解除监管申请已通过批准");
            }
        }

        // 设置申请信息
        releaseSupervision.setInstitutionName(institution.getInstitutionName());
        releaseSupervision.setCreditCode(institution.getCreditCode());
        releaseSupervision.setLegalPerson(institution.getLegalPerson());
        releaseSupervision.setContactPerson(institution.getContactPerson());
        releaseSupervision.setContactPhone(institution.getContactPhone());
        releaseSupervision.setSupervisionBank(institution.getSuperviseBank());
        releaseSupervision.setSupervisionAccount(institution.getSuperviseAccount());
        releaseSupervision.setBasicBank(institution.getBasicBank());
        releaseSupervision.setBasicAccount(institution.getBankAccount());

        // 生成申请编号
        String applyNo = "REL" + System.currentTimeMillis();
        releaseSupervision.setApplyNo(applyNo);

        // 设置申请状态为待审批
        releaseSupervision.setApplyStatus("0");
        releaseSupervision.setApplyTime(new java.util.Date());

        // 插入申请记录
        int result = releaseSupervisionService.insertReleaseSupervision(releaseSupervision);

        if (result > 0) {
            // 保存附件
            releaseSupervisionService.saveAttachments(releaseSupervision.getReleaseId(), releaseSupervision.getAttachments());

            // 更新机构状态为"解除监管审批中"
            PensionInstitution updateInst = new PensionInstitution();
            updateInst.setInstitutionId(institutionId);
            updateInst.setStatus("7"); // 7=解除监管审批中
            pensionInstitutionService.updatePensionInstitution(updateInst);
        }

        return toAjax(result);
    }

    /**
     * 查询机构的解除监管申请状态
     */
    @GetMapping("/release/status/{institutionId}")
    public AjaxResult getReleaseApplyStatus(@PathVariable Long institutionId)
    {
        ReleaseSupervision query = new ReleaseSupervision();
        query.setInstitutionId(institutionId);
        List<ReleaseSupervision> list = releaseSupervisionService.selectReleaseSupervisionList(query);

        if (list == null || list.isEmpty()) {
            return AjaxResult.success().put("hasApply", false);
        }

        // 返回最新的申请详情
        ReleaseSupervision latest = list.get(0);

        // 动态查询机构监管账户余额
        String balanceSql = "SELECT " +
                             "COALESCE(SUM(total_balance), 0) as supervisionBalance, " +
                             "COALESCE(SUM(service_balance), 0) as serviceFeeBalance, " +
                             "COALESCE(SUM(deposit_balance), 0) as depositBalance, " +
                             "COALESCE(SUM(member_balance), 0) as memberFeeBalance " +
                             "FROM account_info WHERE institution_id = ?";
        Map<String, Object> balance = jdbcTemplate.queryForMap(balanceSql, institutionId);

        // 获取附件列表
        List<Map<String, Object>> attachments = releaseSupervisionService.selectAttachmentsByReleaseId(latest.getReleaseId());

        return AjaxResult.success()
                .put("hasApply", true)
                .put("applyStatus", latest.getApplyStatus())
                .put("applyNo", latest.getApplyNo())
                .put("applyTime", latest.getApplyTime())
                .put("approveTime", latest.getApproveTime())
                .put("approver", latest.getApprover())
                .put("releaseReason", latest.getReleaseReason())
                .put("rejectReason", latest.getRejectReason())
                .put("approveRemark", latest.getApproveRemark())
                .put("supervisionBalance", balance.get("supervisionBalance"))
                .put("serviceFeeBalance", balance.get("serviceFeeBalance"))
                .put("depositBalance", balance.get("depositBalance"))
                .put("memberFeeBalance", balance.get("memberFeeBalance"))
                .put("attachments", attachments);
    }
}