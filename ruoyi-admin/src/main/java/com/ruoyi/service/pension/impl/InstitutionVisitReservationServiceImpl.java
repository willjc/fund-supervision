package com.ruoyi.service.pension.impl;

import java.util.Date;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.InstitutionVisitReservationMapper;
import com.ruoyi.domain.pension.InstitutionVisitReservation;
import com.ruoyi.service.pension.IInstitutionVisitReservationService;

/**
 * 机构预约参观Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-23
 */
@Service
public class InstitutionVisitReservationServiceImpl implements IInstitutionVisitReservationService
{
    @Autowired
    private InstitutionVisitReservationMapper institutionVisitReservationMapper;

    /**
     * 查询机构预约参观
     *
     * @param reservationId 机构预约参观主键
     * @return 机构预约参观
     */
    @Override
    public InstitutionVisitReservation selectInstitutionVisitReservationByReservationId(Long reservationId)
    {
        return institutionVisitReservationMapper.selectInstitutionVisitReservationByReservationId(reservationId);
    }

    /**
     * 根据预约ID查询预约详情（关联查询）
     *
     * @param reservationId 预约ID
     * @return 机构预约参观
     */
    @Override
    public InstitutionVisitReservation selectInstitutionVisitReservationWithRelationsByReservationId(Long reservationId)
    {
        return institutionVisitReservationMapper.selectInstitutionVisitReservationWithRelationsByReservationId(reservationId);
    }

    /**
     * 查询机构预约参观列表
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 机构预约参观
     */
    @Override
    public List<InstitutionVisitReservation> selectInstitutionVisitReservationList(InstitutionVisitReservation institutionVisitReservation)
    {
        return institutionVisitReservationMapper.selectInstitutionVisitReservationList(institutionVisitReservation);
    }

    /**
     * 查询机构预约参观列表（关联查询）
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 机构预约参观集合
     */
    @Override
    public List<InstitutionVisitReservation> selectInstitutionVisitReservationWithRelationsList(InstitutionVisitReservation institutionVisitReservation)
    {
        return institutionVisitReservationMapper.selectInstitutionVisitReservationWithRelationsList(institutionVisitReservation);
    }

    /**
     * 新增机构预约参观
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 结果
     */
    @Override
    public int insertInstitutionVisitReservation(InstitutionVisitReservation institutionVisitReservation)
    {
        institutionVisitReservation.setCreateTime(DateUtils.getNowDate());
        return institutionVisitReservationMapper.insertInstitutionVisitReservation(institutionVisitReservation);
    }

    /**
     * 修改机构预约参观
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 结果
     */
    @Override
    public int updateInstitutionVisitReservation(InstitutionVisitReservation institutionVisitReservation)
    {
        institutionVisitReservation.setUpdateTime(DateUtils.getNowDate());
        return institutionVisitReservationMapper.updateInstitutionVisitReservation(institutionVisitReservation);
    }

    /**
     * 批量删除机构预约参观
     *
     * @param reservationIds 需要删除的机构预约参观主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionVisitReservationByReservationIds(Long[] reservationIds)
    {
        return institutionVisitReservationMapper.deleteInstitutionVisitReservationByReservationIds(reservationIds);
    }

    /**
     * 删除机构预约参观信息
     *
     * @param reservationId 机构预约参观主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionVisitReservationByReservationId(Long reservationId)
    {
        return institutionVisitReservationMapper.deleteInstitutionVisitReservationByReservationId(reservationId);
    }

    /**
     * 统计机构预约数量
     *
     * @param institutionId 机构ID
     * @param status 状态（可选）
     * @return 预约数量
     */
    @Override
    public int countReservationsByInstitutionId(Long institutionId, String status)
    {
        return institutionVisitReservationMapper.countReservationsByInstitutionId(institutionId, status);
    }

    /**
     * 提交预约参观
     *
     * @param institutionVisitReservation 预约信息
     * @return 结果
     */
    @Override
    @Transactional
    public int submitVisitReservation(InstitutionVisitReservation institutionVisitReservation)
    {
        // 设置初始状态为待参观
        institutionVisitReservation.setStatus(InstitutionVisitReservation.STATUS_PENDING);

        // 生成预约编号
        institutionVisitReservation.setReservationNo("AP" + System.currentTimeMillis());

        // 设置参观人数默认值
        if (institutionVisitReservation.getVisitorCount() == null) {
            institutionVisitReservation.setVisitorCount(1);
        }

        return insertInstitutionVisitReservation(institutionVisitReservation);
    }

    /**
     * 取消预约
     *
     * @param reservationId 预约ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int cancelReservation(Long reservationId, Long userId)
    {
        // 查询预约信息
        InstitutionVisitReservation reservation = selectInstitutionVisitReservationByReservationId(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约信息不存在");
        }

        // 验证权限
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }

        // 验证状态
        if (!InstitutionVisitReservation.STATUS_PENDING.equals(reservation.getStatus())) {
            throw new RuntimeException("当前状态不能取消");
        }

        // 更新状态为已取消
        reservation.setStatus(InstitutionVisitReservation.STATUS_CANCELLED);
        reservation.setUpdateTime(DateUtils.getNowDate());

        return updateInstitutionVisitReservation(reservation);
    }

    /**
     * 标记预约为已完成
     *
     * @param reservationId 预约ID
     * @param handleUser 处理人
     * @param handleRemark 处理备注
     * @return 结果
     */
    @Override
    @Transactional
    public int completeReservation(Long reservationId, String handleUser, String handleRemark)
    {
        // 查询预约信息
        InstitutionVisitReservation reservation = selectInstitutionVisitReservationByReservationId(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约信息不存在");
        }

        // 验证状态
        if (!InstitutionVisitReservation.STATUS_PENDING.equals(reservation.getStatus())) {
            throw new RuntimeException("当前状态不能标记为已完成");
        }

        // 更新状态为已完成
        reservation.setStatus(InstitutionVisitReservation.STATUS_COMPLETED);
        reservation.setHandleUser(handleUser);
        reservation.setHandleTime(new Date());
        reservation.setHandleRemark(handleRemark);
        reservation.setUpdateTime(DateUtils.getNowDate());

        return updateInstitutionVisitReservation(reservation);
    }
}
