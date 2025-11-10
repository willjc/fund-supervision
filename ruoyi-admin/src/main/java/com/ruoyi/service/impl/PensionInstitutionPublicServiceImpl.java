package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.PensionInstitutionPublicMapper;
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.service.IPensionInstitutionPublicService;

/**
 * 养老机构公示信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-10
 */
@Service
public class PensionInstitutionPublicServiceImpl implements IPensionInstitutionPublicService
{
    @Autowired
    private PensionInstitutionPublicMapper pensionInstitutionPublicMapper;

    /**
     * 查询养老机构公示信息
     *
     * @param publicId 养老机构公示信息主键
     * @return 养老机构公示信息
     */
    @Override
    public PensionInstitutionPublic selectPensionInstitutionPublicByPublicId(Long publicId)
    {
        return pensionInstitutionPublicMapper.selectPensionInstitutionPublicByPublicId(publicId);
    }

    /**
     * 根据机构ID查询公示信息
     *
     * @param institutionId 机构ID
     * @return 养老机构公示信息
     */
    @Override
    public PensionInstitutionPublic selectPensionInstitutionPublicByInstitutionId(Long institutionId)
    {
        return pensionInstitutionPublicMapper.selectPensionInstitutionPublicByInstitutionId(institutionId);
    }

    /**
     * 查询养老机构公示信息列表
     *
     * @param pensionInstitutionPublic 养老机构公示信息
     * @return 养老机构公示信息
     */
    @Override
    public List<PensionInstitutionPublic> selectPensionInstitutionPublicList(PensionInstitutionPublic pensionInstitutionPublic)
    {
        return pensionInstitutionPublicMapper.selectPensionInstitutionPublicList(pensionInstitutionPublic);
    }

    /**
     * 新增养老机构公示信息
     *
     * @param pensionInstitutionPublic 养老机构公示信息
     * @return 结果
     */
    @Override
    public int insertPensionInstitutionPublic(PensionInstitutionPublic pensionInstitutionPublic)
    {
        pensionInstitutionPublic.setCreateTime(DateUtils.getNowDate());
        return pensionInstitutionPublicMapper.insertPensionInstitutionPublic(pensionInstitutionPublic);
    }

    /**
     * 修改养老机构公示信息
     *
     * @param pensionInstitutionPublic 养老机构公示信息
     * @return 结果
     */
    @Override
    public int updatePensionInstitutionPublic(PensionInstitutionPublic pensionInstitutionPublic)
    {
        pensionInstitutionPublic.setUpdateTime(DateUtils.getNowDate());
        return pensionInstitutionPublicMapper.updatePensionInstitutionPublic(pensionInstitutionPublic);
    }

    /**
     * 批量删除养老机构公示信息
     *
     * @param publicIds 需要删除的养老机构公示信息主键
     * @return 结果
     */
    @Override
    public int deletePensionInstitutionPublicByPublicIds(Long[] publicIds)
    {
        return pensionInstitutionPublicMapper.deletePensionInstitutionPublicByPublicIds(publicIds);
    }

    /**
     * 删除养老机构公示信息信息
     *
     * @param publicId 养老机构公示信息主键
     * @return 结果
     */
    @Override
    public int deletePensionInstitutionPublicByPublicId(Long publicId)
    {
        return pensionInstitutionPublicMapper.deletePensionInstitutionPublicByPublicId(publicId);
    }

    /**
     * 发布公示
     *
     * @param publicId 公示ID
     * @return 结果
     */
    @Override
    public int publishPublicity(Long publicId)
    {
        return pensionInstitutionPublicMapper.publishPublicity(publicId);
    }

    /**
     * 取消公示
     *
     * @param publicId 公示ID
     * @return 结果
     */
    @Override
    public int unpublishPublicity(Long publicId)
    {
        return pensionInstitutionPublicMapper.unpublishPublicity(publicId);
    }
}
