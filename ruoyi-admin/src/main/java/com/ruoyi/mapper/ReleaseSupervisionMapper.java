package com.ruoyi.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.ReleaseSupervision;

/**
 * 机构解除监管申请Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-04
 */
public interface ReleaseSupervisionMapper
{
    /**
     * 查询机构解除监管申请
     *
     * @param releaseId 机构解除监管申请主键
     * @return 机构解除监管申请
     */
    public ReleaseSupervision selectReleaseSupervisionByReleaseId(Long releaseId);

    /**
     * 查询机构解除监管申请列表
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 机构解除监管申请集合
     */
    public List<ReleaseSupervision> selectReleaseSupervisionList(ReleaseSupervision releaseSupervision);

    /**
     * 新增机构解除监管申请
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 结果
     */
    public int insertReleaseSupervision(ReleaseSupervision releaseSupervision);

    /**
     * 修改机构解除监管申请
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 结果
     */
    public int updateReleaseSupervision(ReleaseSupervision releaseSupervision);

    /**
     * 删除机构解除监管申请
     *
     * @param releaseId 机构解除监管申请主键
     * @return 结果
     */
    public int deleteReleaseSupervisionByReleaseId(Long releaseId);

    /**
     * 批量删除机构解除监管申请
     *
     * @param releaseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteReleaseSupervisionByReleaseIds(Long[] releaseIds);

    /**
     * 获取统计数据
     *
     * @return 统计数据
     */
    public Map<String, Object> selectReleaseStatistics();
}
