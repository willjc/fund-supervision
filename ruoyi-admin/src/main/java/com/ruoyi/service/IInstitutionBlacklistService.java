package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.InstitutionBlacklist;

/**
 * 机构黑名单Service接口
 *
 * @author ruoyi
 * @date 2025-01-31
 */
public interface IInstitutionBlacklistService
{
    /**
     * 查询机构黑名单
     *
     * @param id 机构黑名单主键
     * @return 机构黑名单
     */
    public InstitutionBlacklist selectInstitutionBlacklistById(Long id);

    /**
     * 查询机构黑名单列表
     *
     * @param institutionBlacklist 机构黑名单
     * @return 机构黑名单集合
     */
    public List<InstitutionBlacklist> selectInstitutionBlacklistList(InstitutionBlacklist institutionBlacklist);

    /**
     * 根据机构ID查询黑名单记录
     *
     * @param institutionId 机构ID
     * @return 机构黑名单
     */
    public InstitutionBlacklist selectByInstitutionId(Long institutionId);

    /**
     * 新增机构黑名单
     *
     * @param institutionBlacklist 机构黑名单
     * @return 结果
     */
    public int insertInstitutionBlacklist(InstitutionBlacklist institutionBlacklist);

    /**
     * 修改机构黑名单
     *
     * @param institutionBlacklist 机构黑名单
     * @return 结果
     */
    public int updateInstitutionBlacklist(InstitutionBlacklist institutionBlacklist);

    /**
     * 批量删除机构黑名单
     *
     * @param ids 需要删除的机构黑名单主键集合
     * @return 结果
     */
    public int deleteInstitutionBlacklistByIds(Long[] ids);

    /**
     * 删除机构黑名单信息
     *
     * @param id 机构黑名单主键
     * @return 结果
     */
    public int deleteInstitutionBlacklistById(Long id);

    /**
     * 移除黑名单（将状态改为已解除）
     *
     * @param id 机构黑名单主键
     * @return 结果
     */
    public int removeFromBlacklist(Long id);

    /**
     * 将机构加入黑名单
     *
     * @param institutionIds 机构ID数组
     * @param blacklistType 黑名单类型
     * @param reason 原因描述
     * @return 结果
     */
    public int addToBlacklist(Long[] institutionIds, String blacklistType, String reason);
}
