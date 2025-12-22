package com.ruoyi.web.controller.pension;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.pension.InstitutionVisitReservation;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.service.pension.IInstitutionVisitReservationService;

/**
 * 预约参观管理Controller
 *
 * @author ruoyi
 * @date 2025-12-23
 */
@RestController
@RequestMapping("/pension/visit")
public class VisitReservationController extends BaseController
{
    @Autowired
    private IInstitutionVisitReservationService visitReservationService;

    /**
     * 查询预约参观列表
     */
    @PreAuthorize("@ss.hasPermi('pension:visit:list')")
    @GetMapping("/list")
    public TableDataInfo list(InstitutionVisitReservation institutionVisitReservation)
    {
        startPage();

        // 获取当前用户关联的机构ID（数据权限控制）
        // 机构管理员只能查看自己机构的预约
        Long institutionId = getInstitutionIdFromUser();
        if (institutionId != null) {
            institutionVisitReservation.setInstitutionId(institutionId);
        }

        List<InstitutionVisitReservation> list = visitReservationService.selectInstitutionVisitReservationWithRelationsList(institutionVisitReservation);
        return getDataTable(list);
    }

    /**
     * 导出预约参观列表
     */
    @PreAuthorize("@ss.hasPermi('pension:visit:export')")
    @Log(title = "预约参观", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InstitutionVisitReservation institutionVisitReservation)
    {
        // 获取当前用户关联的机构ID（数据权限控制）
        Long institutionId = getInstitutionIdFromUser();
        if (institutionId != null) {
            institutionVisitReservation.setInstitutionId(institutionId);
        }

        List<InstitutionVisitReservation> list = visitReservationService.selectInstitutionVisitReservationWithRelationsList(institutionVisitReservation);
        ExcelUtil<InstitutionVisitReservation> util = new ExcelUtil<InstitutionVisitReservation>(InstitutionVisitReservation.class);
        util.exportExcel(response, list, "预约参观数据");
    }

    /**
     * 获取预约参观详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:visit:query')")
    @GetMapping(value = "/{reservationId}")
    public AjaxResult getInfo(@PathVariable("reservationId") Long reservationId)
    {
        InstitutionVisitReservation reservation = visitReservationService.selectInstitutionVisitReservationWithRelationsByReservationId(reservationId);

        // 数据权限验证
        Long institutionId = getInstitutionIdFromUser();
        if (institutionId != null && !reservation.getInstitutionId().equals(institutionId)) {
            return error("无权访问该预约信息");
        }

        return success(reservation);
    }

    /**
     * 标记预约为已完成
     */
    @PreAuthorize("@ss.hasPermi('pension:visit:complete')")
    @Log(title = "预约参观", businessType = BusinessType.UPDATE)
    @PostMapping("/complete")
    public AjaxResult complete(@RequestBody Map<String, Object> params)
    {
        Long reservationId = Long.valueOf(params.get("reservationId").toString());
        String handleRemark = params.get("handleRemark") != null ? params.get("handleRemark").toString() : null;

        // 权限验证
        InstitutionVisitReservation reservation = visitReservationService.selectInstitutionVisitReservationByReservationId(reservationId);
        if (reservation == null) {
            return error("预约信息不存在");
        }

        Long institutionId = getInstitutionIdFromUser();
        if (institutionId != null && !reservation.getInstitutionId().equals(institutionId)) {
            return error("无权操作该预约信息");
        }

        String handleUser = SecurityUtils.getUsername();

        try {
            int result = visitReservationService.completeReservation(reservationId, handleUser, handleRemark);
            return toAjax(result);
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 获取当前用户关联的机构ID
     * 用于数据权限控制
     * 如果是机构管理员，返回其管理的机构ID
     * 如果是超级管理员或其他角色，返回null（不限制）
     */
    private Long getInstitutionIdFromUser() {
        try {
            // 获取当前用户名
            String username = SecurityUtils.getUsername();

            // 如果是超级管理员，不限制数据权限
            if ("admin".equals(username)) {
                return null;
            }

            // TODO: 根据实际业务逻辑获取机构管理员关联的机构ID
            // 这里可以通过查询用户表或机构关联表来获取
            // 示例：从用户属性或关联表中获取机构ID

            // 暂时返回null，表示不限制
            // 实际项目中应该根据业务逻辑实现
            return null;

        } catch (Exception e) {
            logger.error("获取用户关联机构ID失败", e);
            return null;
        }
    }
}
