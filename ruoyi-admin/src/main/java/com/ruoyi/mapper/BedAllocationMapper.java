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
}
