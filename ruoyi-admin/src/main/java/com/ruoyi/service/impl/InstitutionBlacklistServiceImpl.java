package com.ruoyi.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.mapper.InstitutionBlacklistMapper;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.domain.InstitutionBlacklist;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IInstitutionBlacklistService;

/**
 * 机构黑名单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-31
 */
@Service
public class InstitutionBlacklistServiceImpl implements IInstitutionBlacklistService
{
    @Autowired
    private InstitutionBlacklistMapper institutionBlacklistMapper;

    @Autowired
    private PensionInstitutionMapper pensionInstitutionMapper;

    /**
     * 查询机构黑名单
     *
     * @param id 机构黑名单主键
     * @return 机构黑名单
     */
    @Override
    public InstitutionBlacklist selectInstitutionBlacklistById(Long id)
    {
        return institutionBlacklistMapper.selectInstitutionBlacklistById(id);
    }

    /**
     * 查询机构黑名单列表
     *
     * @param institutionBlacklist 机构黑名单
     * @return 机构黑名单
     */
    @Override
    public List<InstitutionBlacklist> selectInstitutionBlacklistList(InstitutionBlacklist institutionBlacklist)
    {
        return institutionBlacklistMapper.selectInstitutionBlacklistList(institutionBlacklist);
    }

    /**
     * 根据机构ID查询黑名单记录
     *
     * @param institutionId 机构ID
     * @return 机构黑名单
     */
    @Override
    public InstitutionBlacklist selectByInstitutionId(Long institutionId)
    {
        return institutionBlacklistMapper.selectByInstitutionId(institutionId);
    }

    /**
     * 新增机构黑名单
     *
     * @param institutionBlacklist 机构黑名单
     * @return 结果
     */
    @Override
    public int insertInstitutionBlacklist(InstitutionBlacklist institutionBlacklist)
    {
        institutionBlacklist.setCreateTime(new Date());
        return institutionBlacklistMapper.insertInstitutionBlacklist(institutionBlacklist);
    }

    /**
     * 修改机构黑名单
     * 同步更新机构表的黑名单标志
     *
     * @param institutionBlacklist 机构黑名单
     * @return 结果
     */
    @Override
    @Transactional
    public int updateInstitutionBlacklist(InstitutionBlacklist institutionBlacklist)
    {
        institutionBlacklist.setUpdateTime(new Date());

        // 获取更新前的黑名单记录，用于判断状态是否发生变化
        InstitutionBlacklist oldRecord = null;
        if (institutionBlacklist.getId() != null) {
            oldRecord = institutionBlacklistMapper.selectInstitutionBlacklistById(institutionBlacklist.getId());
        }

        int result = institutionBlacklistMapper.updateInstitutionBlacklist(institutionBlacklist);

        // 如果状态发生变化，同步更新机构表的黑名单标志
        if (result > 0 && oldRecord != null && institutionBlacklist.getStatus() != null
                && !oldRecord.getStatus().equals(institutionBlacklist.getStatus())
                && institutionBlacklist.getInstitutionId() != null) {
            PensionInstitution institution = new PensionInstitution();
            institution.setInstitutionId(institutionBlacklist.getInstitutionId());

            // status='1' 表示生效中，设置黑名单标志为'1'
            // status='2' 表示已解除，设置黑名单标志为'0'
            institution.setBlacklistFlag("1".equals(institutionBlacklist.getStatus()) ? "1" : "0");
            institution.setUpdateBy(SecurityUtils.getUsername());
            institution.setUpdateTime(new Date());
            pensionInstitutionMapper.updatePensionInstitution(institution);
        }

        return result;
    }

    /**
     * 批量删除机构黑名单
     *
     * @param ids 需要删除的机构黑名单主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionBlacklistByIds(Long[] ids)
    {
        return institutionBlacklistMapper.deleteInstitutionBlacklistByIds(ids);
    }

    /**
     * 删除机构黑名单信息
     *
     * @param id 机构黑名单主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionBlacklistById(Long id)
    {
        return institutionBlacklistMapper.deleteInstitutionBlacklistById(id);
    }

    /**
     * 移除黑名单（将状态改为已解除）
     *
     * @param id 机构黑名单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int removeFromBlacklist(Long id)
    {
        InstitutionBlacklist blacklist = institutionBlacklistMapper.selectInstitutionBlacklistById(id);
        if (blacklist == null)
        {
            return 0;
        }

        // 移除黑名单
        int result = institutionBlacklistMapper.removeFromBlacklist(id);

        // 同时更新机构表的黑名单标志
        if (result > 0 && blacklist.getInstitutionId() != null)
        {
            PensionInstitution institution = new PensionInstitution();
            institution.setInstitutionId(blacklist.getInstitutionId());
            institution.setBlacklistFlag("0");
            institution.setUpdateBy(SecurityUtils.getUsername());
            institution.setUpdateTime(new Date());
            pensionInstitutionMapper.updatePensionInstitution(institution);
        }

        return result;
    }

    /**
     * 将机构加入黑名单
     *
     * @param institutionIds 机构ID数组
     * @param blacklistType 黑名单类型
     * @param reason 原因描述
     * @return 结果
     */
    @Override
    @Transactional
    public int addToBlacklist(Long[] institutionIds, String blacklistType, String reason)
    {
        int count = 0;
        String operator = SecurityUtils.getUsername();
        Date now = new Date();

        for (Long institutionId : institutionIds)
        {
            // 查询机构信息
            PensionInstitution institution = pensionInstitutionMapper.selectPensionInstitutionByInstitutionId(institutionId);
            if (institution == null)
            {
                continue;
            }

            // 检查是否已在黑名单中
            InstitutionBlacklist existBlacklist = institutionBlacklistMapper.selectByInstitutionId(institutionId);
            if (existBlacklist != null && "1".equals(existBlacklist.getStatus()))
            {
                // 已在黑名单中，跳过
                continue;
            }

            // 创建黑名单记录
            InstitutionBlacklist blacklist = new InstitutionBlacklist();
            blacklist.setInstitutionId(institutionId);
            blacklist.setInstitutionName(institution.getInstitutionName());
            blacklist.setBlacklistType(blacklistType != null && !blacklistType.trim().isEmpty() ? blacklistType : "其他违规");
            blacklist.setReason(reason);
            blacklist.setStatus("1");
            blacklist.setAddTime(now);
            blacklist.setCreateBy(operator);

            institutionBlacklistMapper.insertInstitutionBlacklist(blacklist);

            // 更新机构表的黑名单标志
            institution.setBlacklistFlag("1");
            institution.setUpdateBy(operator);
            institution.setUpdateTime(now);
            pensionInstitutionMapper.updatePensionInstitution(institution);

            count++;
        }

        return count;
    }
}
