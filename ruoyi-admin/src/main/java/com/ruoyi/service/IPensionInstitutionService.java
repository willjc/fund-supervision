package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.PensionInstitution;

/**
 * 养老机构信息Service接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface IPensionInstitutionService
{
    /**
     * 查询养老机构信息
     *
     * @param institutionId 养老机构信息主键
     * @return 养老机构信息
     */
    public PensionInstitution selectPensionInstitutionByInstitutionId(Long institutionId);

    /**
     * 查询养老机构信息列表
     *
     * @param pensionInstitution 养老机构信息
     * @return 养老机构信息集合
     */
    public List<PensionInstitution> selectPensionInstitutionList(PensionInstitution pensionInstitution);

    /**
     * 新增养老机构信息
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    public int insertPensionInstitution(PensionInstitution pensionInstitution);

    /**
     * 修改养老机构信息
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    public int updatePensionInstitution(PensionInstitution pensionInstitution);

    /**
     * 批量删除养老机构信息
     *
     * @param institutionIds 需要删除的养老机构信息主键集合
     * @return 结果
     */
    public int deletePensionInstitutionByInstitutionIds(Long[] institutionIds);

    /**
     * 删除养老机构信息信息
     *
     * @param institutionId 养老机构信息主键
     * @return 结果
     */
    public int deletePensionInstitutionByInstitutionId(Long institutionId);

    /**
     * 审批机构入驻申请
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    public int approveInstitution(PensionInstitution pensionInstitution);

    /**
     * 更新草稿(强制清除审批相关字段)
     *
     * @param pensionInstitution 养老机构信息
     * @return 结果
     */
    public int updateDraft(PensionInstitution pensionInstitution);
}