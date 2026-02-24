package com.ruoyi.mapper;

import com.ruoyi.domain.BedAllocation;

/**
 * 床位分配Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-11
 */
public interface BedAllocationMapper
{
    /**
     * 新增床位分配记录
     *
     * @param bedAllocation 床位分配信息
     * @return 结果
     */
    public int insertBedAllocation(BedAllocation bedAllocation);

    /**
     * 查询床位分配记录
     *
     * @param allocationId 分配记录ID
     * @return 床位分配信息
     */
    public BedAllocation selectBedAllocationById(Long allocationId);

    /**
     * 更新床位分配状态
     *
     * @param allocationId 分配记录ID
     * @param status 状态
     * @return 结果
     */
    public int updateAllocationStatus(Long allocationId, String status);

    /**
     * 根据老人ID查询当前有效的床位分配记录
     *
     * @param elderId 老人ID
     * @return 床位分配信息
     */
    public BedAllocation selectBedAllocationByElderId(Long elderId);

    /**
     * 更新床位分配记录
     *
     * @param bedAllocation 床位分配信息
     * @return 结果
     */
    public int updateBedAllocation(BedAllocation bedAllocation);

    /**
     * 根据床位ID删除床位分配记录（取消订单时释放床位）
     *
     * @param bedId 床位ID
     * @return 结果
     */
    public int deleteBedAllocationByBedId(Long bedId);

    /**
     * 根据老人ID删除床位分配记录
     *
     * @param elderId 老人ID
     * @return 结果
     */
    public int deleteBedAllocationByElderId(Long elderId);

    /**
     * 根据老人ID更新床位ID（审核通过时同步床位信息）
     *
     * @param elderId 老人ID
     * @param bedId 新床位ID
     * @return 结果
     */
    public int updateBedIdByElderId(@org.apache.ibatis.annotations.Param("elderId") Long elderId,
                                     @org.apache.ibatis.annotations.Param("bedId") Long bedId);

    /**
     * 根据老人ID和机构ID更新床位ID（审核通过时同步床位信息）
     *
     * @param elderId 老人ID
     * @param bedId 新床位ID
     * @param institutionId 机构ID
     * @return 结果
     */
    public int updateBedIdByElderIdAndInstitutionId(@org.apache.ibatis.annotations.Param("elderId") Long elderId,
                                                    @org.apache.ibatis.annotations.Param("bedId") Long bedId,
                                                    @org.apache.ibatis.annotations.Param("institutionId") Long institutionId);

    /**
     * 根据老人ID和机构ID查询未退住的床位分配记录
     *
     * @param elderId 老人ID
     * @param institutionId 机构ID
     * @return 床位分配信息
     */
    public BedAllocation selectActiveByElderAndInstitution(@org.apache.ibatis.annotations.Param("elderId") Long elderId,
                                                             @org.apache.ibatis.annotations.Param("institutionId") Long institutionId);
}
