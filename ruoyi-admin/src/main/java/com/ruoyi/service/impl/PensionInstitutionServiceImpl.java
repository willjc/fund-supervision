package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IPensionInstitutionService;

/**
 * 养老机构信息Service业务层处��
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class PensionInstitutionServiceImpl implements IPensionInstitutionService
{
    @Autowired
    private PensionInstitutionMapper pensionInstitutionMapper;

    /**
     * 查询养老机构信息
     *
     * @param institutionId 养老机构信息主键
     * @return 养老机构信息
     */
    @Override
    public PensionInstitution selectPensionInstitutionByInstitutionId(Long institutionId)
    {
        return pensionInstitutionMapper.selectPensionInstitutionByInstitutionId(institutionId);
    }

    /**
     * 查询养老机构信息列表
     *
     * @param pensionInstitution 养老机构信息
     * @return 养老机构信息
     */
    @Override
    public List<PensionInstitution> selectPensionInstitutionList(PensionInstitution pensionInstitution)
    {
        // 处理前端传递的多选参数，避免空集合查询
        if (pensionInstitution.getAreaCodes() != null && pensionInstitution.getAreaCodes().isEmpty()) {
            pensionInstitution.setAreaCodes(null);
        }
        if (pensionInstitution.getStreetNames() != null && pensionInstitution.getStreetNames().isEmpty()) {
            pensionInstitution.setStreetNames(null);
        }

        return pensionInstitutionMapper.selectPensionInstitutionList(pensionInstitution);
    }

    /**
     * 新增养老机构信息
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    @Override
    public int insertPensionInstitution(PensionInstitution pensionInstitution)
    {
        pensionInstitution.setCreateTime(DateUtils.getNowDate());
        // 不再强制设置status和applyTime,由Controller层根据业务场景设置
        // 草稿: status=4, applyTime=null
        // 提交: status=0, applyTime=当前时间
        return pensionInstitutionMapper.insertPensionInstitution(pensionInstitution);
    }

    /**
     * 修改养老机构信息
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    @Override
    public int updatePensionInstitution(PensionInstitution pensionInstitution)
    {
        pensionInstitution.setUpdateTime(DateUtils.getNowDate());
        return pensionInstitutionMapper.updatePensionInstitution(pensionInstitution);
    }

    /**
     * 批量删除养老机构信息
     *
     * @param institutionIds 需要删除的养老机构信息主键
     * @return 结果
     */
    @Override
    public int deletePensionInstitutionByInstitutionIds(Long[] institutionIds)
    {
        return pensionInstitutionMapper.deletePensionInstitutionByInstitutionIds(institutionIds);
    }

    /**
     * 删除养老机构信息信息
     *
     * @param institutionId 养老机构信息主键
     * @return 结果
     */
    @Override
    public int deletePensionInstitutionByInstitutionId(Long institutionId)
    {
        return pensionInstitutionMapper.deletePensionInstitutionByInstitutionId(institutionId);
    }

    /**
     * 审批机构入驻申请
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    @Override
    public int approveInstitution(PensionInstitution pensionInstitution)
    {
        pensionInstitution.setApproveTime(DateUtils.getNowDate());
        return pensionInstitutionMapper.approveInstitution(pensionInstitution);
    }

    /**
     * 更新草稿(强制清除审批相关字段)
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    @Override
    public int updateDraft(PensionInstitution pensionInstitution)
    {
        pensionInstitution.setUpdateTime(DateUtils.getNowDate());
        return pensionInstitutionMapper.updateDraft(pensionInstitution);
    }
}