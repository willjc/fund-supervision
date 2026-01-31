package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.InstitutionBlacklist;

/**
 * 机构黑名单Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-31
 */
public interface InstitutionBlacklistMapper
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
     * 删除机构黑名单
     *
     * @param id 机构黑名单主键
     * @return 结果
     */
    public int deleteInstitutionBlacklistById(Long id);

    /**
     * 批量删除机构黑名单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInstitutionBlacklistByIds(Long[] ids);

    /**
     * 移除黑名单（将状态改为已解除）
     *
     * @param id 机构黑名单主键
     * @return 结果
     */
    public int removeFromBlacklist(Long id);
}
