package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.InstitutionVisitReservation;

/**
 * 机构预约参观Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-23
 */
public interface InstitutionVisitReservationMapper
{
    /**
     * 查询机构预约参观
     *
     * @param reservationId 机构预约参观主键
     * @return 机构预约参观
     */
    public InstitutionVisitReservation selectInstitutionVisitReservationByReservationId(Long reservationId);

    /**
     * 查询机构预约参观列表
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 机构预约参观集合
     */
    public List<InstitutionVisitReservation> selectInstitutionVisitReservationList(InstitutionVisitReservation institutionVisitReservation);

    /**
     * 查询机构预约参观列表（关联查询）
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 机构预约参观集合
     */
    public List<InstitutionVisitReservation> selectInstitutionVisitReservationWithRelationsList(InstitutionVisitReservation institutionVisitReservation);

    /**
     * 根据预约ID查询预约详情（关联查询）
     *
     * @param reservationId 预约ID
     * @return 机构预约参观
     */
    public InstitutionVisitReservation selectInstitutionVisitReservationWithRelationsByReservationId(Long reservationId);

    /**
     * 新增机构预约参观
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 结果
     */
    public int insertInstitutionVisitReservation(InstitutionVisitReservation institutionVisitReservation);

    /**
     * 修改机构预约参观
     *
     * @param institutionVisitReservation 机构预约参观
     * @return 结果
     */
    public int updateInstitutionVisitReservation(InstitutionVisitReservation institutionVisitReservation);

    /**
     * 删除机构预约参观
     *
     * @param reservationId 机构预约参观主键
     * @return 结果
     */
    public int deleteInstitutionVisitReservationByReservationId(Long reservationId);

    /**
     * 批量删除机构预约参观
     *
     * @param reservationIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInstitutionVisitReservationByReservationIds(Long[] reservationIds);

    /**
     * 统计机构预约数量
     *
     * @param institutionId 机构ID
     * @param status 状态（可选）
     * @return 预约数量
     */
    public int countReservationsByInstitutionId(Long institutionId, String status);
}
