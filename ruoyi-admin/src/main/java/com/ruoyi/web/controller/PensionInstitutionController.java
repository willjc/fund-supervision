package com.ruoyi.web.controller;

import java.util.List;
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
        // 只显示已审核通过的机构（status='1'），入驻申请等场景只能选择已通过审核的机构
        pensionInstitution.setStatus("1");
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        return getDataTable(list);
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

        // 检查是否是从草稿提交(根据统一信用代码查询草稿)
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
                System.out.println("设置status=0, applyTime=" + pensionInstitution.getApplyTime());
                return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
            }
        }

        // 新增申请,直接提交审核
        System.out.println("执行新增并提交申请");
        pensionInstitution.setStatus("0");  // 0表示待审批状态
        pensionInstitution.setApplyTime(new java.util.Date());
        System.out.println("设置status=0, applyTime=" + pensionInstitution.getApplyTime());
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

        // 检查是否已有草稿(根据统一信用代码查询)
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
}