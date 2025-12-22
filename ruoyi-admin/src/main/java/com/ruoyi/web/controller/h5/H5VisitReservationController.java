package com.ruoyi.web.controller.h5;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.domain.pension.InstitutionVisitReservation;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.service.pension.IInstitutionVisitReservationService;

/**
 * H5预约参观Controller
 *
 * @author ruoyi
 * @date 2025-12-23
 */
@RestController
@RequestMapping("/h5/visit")
public class H5VisitReservationController extends BaseController
{
    @Autowired
    private IInstitutionVisitReservationService visitReservationService;

    @Autowired
    private IPensionInstitutionPublicService institutionPublicService;

    /**
     * 提交预约参观
     */
    @PostMapping("/submit")
    public AjaxResult submitVisit(@RequestBody Map<String, Object> visitData)
    {
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 解析预约数据
            Long institutionId = Long.valueOf(visitData.get("institutionId").toString());
            String visitorName = visitData.get("visitorName").toString();
            String visitorPhone = visitData.get("visitorPhone").toString();
            String visitDateStr = visitData.get("visitDate").toString();
            String visitTime = visitData.get("visitTime").toString();
            Integer visitorCount = Integer.valueOf(visitData.get("visitorCount").toString());
            String remark = visitData.get("remark") != null ? visitData.get("remark").toString() : null;

            // 解析日期
            Date visitDate = DateUtils.parseDate(visitDateStr);

            // 创建预约对象
            InstitutionVisitReservation reservation = new InstitutionVisitReservation();
            reservation.setInstitutionId(institutionId);
            reservation.setUserId(userId);
            reservation.setVisitorName(visitorName);
            reservation.setVisitorPhone(visitorPhone);
            reservation.setVisitDate(visitDate);
            reservation.setVisitTime(visitTime);
            reservation.setVisitorCount(visitorCount);
            reservation.setRemark(remark);

            // 提交预约
            int result = visitReservationService.submitVisitReservation(reservation);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("reservationId", reservation.getReservationId());
                responseData.put("reservationNo", reservation.getReservationNo());
                responseData.put("message", "预约提交成功");
                return success(responseData);
            } else {
                return error("预约提交失败");
            }
        } catch (Exception e) {
            logger.error("提交预约参观失败", e);
            return error("预约提交失败：" + e.getMessage());
        }
    }

    /**
     * 获取我的预约列表
     */
    @GetMapping("/my")
    public AjaxResult getMyVisits(@RequestParam(required = false) String status,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize)
    {
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 构建查询条件
            InstitutionVisitReservation query = new InstitutionVisitReservation();
            query.setUserId(userId);

            // 状态过滤
            if (status != null && !status.isEmpty() && !"all".equals(status)) {
                query.setStatus(status);
            }

            // 查询预约列表（关联查询）
            List<InstitutionVisitReservation> reservationList =
                visitReservationService.selectInstitutionVisitReservationWithRelationsList(query);

            // 分页处理
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = startIndex + pageSize;

            List<InstitutionVisitReservation> paginatedList = new java.util.ArrayList<>();
            if (reservationList != null) {
                // 按创建时间倒序排序
                reservationList.sort((r1, r2) -> {
                    if (r2.getCreateTime() == null || r1.getCreateTime() == null) {
                        return 0;
                    }
                    return r2.getCreateTime().compareTo(r1.getCreateTime());
                });

                // 分页截取
                for (int i = startIndex; i < endIndex && i < reservationList.size(); i++) {
                    paginatedList.add(reservationList.get(i));
                }
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            java.util.List<Map<String, Object>> reservationListData = new java.util.ArrayList<>();

            for (InstitutionVisitReservation reservation : paginatedList) {
                Map<String, Object> item = new HashMap<>();
                item.put("reservationId", reservation.getReservationId());
                item.put("reservationNo", reservation.getReservationNo());
                item.put("institutionId", reservation.getInstitutionId());
                item.put("institutionName", reservation.getInstitutionName());

                // 获取机构公示信息中的主图
                PensionInstitutionPublic publicInfo = institutionPublicService.selectPensionInstitutionPublicByInstitutionId(reservation.getInstitutionId());
                if (publicInfo != null && publicInfo.getMainPicture() != null && !publicInfo.getMainPicture().isEmpty()) {
                    item.put("institutionCover", publicInfo.getMainPicture());
                } else {
                    item.put("institutionCover", "");
                }

                item.put("visitorName", reservation.getVisitorName());
                item.put("visitorPhone", reservation.getVisitorPhone());
                item.put("visitDate", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, reservation.getVisitDate()));
                item.put("visitTime", reservation.getVisitTime());
                item.put("visitorCount", reservation.getVisitorCount());
                item.put("status", reservation.getStatus());
                item.put("statusText", reservation.getStatusText());
                item.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, reservation.getCreateTime()));

                reservationListData.add(item);
            }

            result.put("rows", reservationListData);
            result.put("total", reservationList != null ? reservationList.size() : 0);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            return success(result);
        } catch (Exception e) {
            logger.error("获取我的预约列表失败", e);
            return error("获取预约列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{reservationId}")
    public AjaxResult getVisitDetail(@PathVariable Long reservationId)
    {
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 查询预约信息
            InstitutionVisitReservation reservation =
                visitReservationService.selectInstitutionVisitReservationWithRelationsByReservationId(reservationId);

            if (reservation == null) {
                return error("预约信息不存在");
            }

            // 验证权限
            if (!reservation.getUserId().equals(userId)) {
                return error("无权访问该预约信息");
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("reservationId", reservation.getReservationId());
            result.put("reservationNo", reservation.getReservationNo());
            result.put("institutionId", reservation.getInstitutionId());
            result.put("institutionName", reservation.getInstitutionName());

            // 获取机构公示信息中的主图
            PensionInstitutionPublic publicInfo = institutionPublicService.selectPensionInstitutionPublicByInstitutionId(reservation.getInstitutionId());
            if (publicInfo != null && publicInfo.getMainPicture() != null && !publicInfo.getMainPicture().isEmpty()) {
                result.put("institutionCover", publicInfo.getMainPicture());
            } else {
                result.put("institutionCover", "");
            }

            result.put("visitorName", reservation.getVisitorName());
            result.put("visitorPhone", reservation.getVisitorPhone());
            result.put("visitDate", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, reservation.getVisitDate()));
            result.put("visitTime", reservation.getVisitTime());
            result.put("visitorCount", reservation.getVisitorCount());
            result.put("remark", reservation.getRemark());
            result.put("status", reservation.getStatus());
            result.put("statusText", reservation.getStatusText());
            result.put("handleUser", reservation.getHandleUser());
            result.put("handleTime", reservation.getHandleTime() != null ?
                DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, reservation.getHandleTime()) : null);
            result.put("handleRemark", reservation.getHandleRemark());
            result.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, reservation.getCreateTime()));

            return success(result);
        } catch (Exception e) {
            logger.error("获取预约详情失败", e);
            return error("获取预约详情失败：" + e.getMessage());
        }
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel/{reservationId}")
    public AjaxResult cancelVisit(@PathVariable Long reservationId)
    {
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 取消预约
            int result = visitReservationService.cancelReservation(reservationId, userId);
            if (result > 0) {
                return success("预约取消成功");
            } else {
                return error("预约取消失败");
            }
        } catch (RuntimeException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            logger.error("取消预约失败", e);
            return error("取消预约失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户的ID
     */
    private Long getCurrentUserId() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            logger.warn("获取当前用户ID失败，用户可能未登录", e);
            return null;
        }
    }
}
