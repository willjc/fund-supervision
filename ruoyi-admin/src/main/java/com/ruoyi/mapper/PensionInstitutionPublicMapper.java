package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.PensionInstitutionPublic;

/**
 * 养老机构公示信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-10
 */
public interface PensionInstitutionPublicMapper
{
    /**
     * 查询养老机构公示信息
     *
     * @param publicId 养老机构公示信息主键
     * @return 养老机构公示信息
     */
    public PensionInstitutionPublic selectPensionInstitutionPublicByPublicId(Long publicId);

    /**
     * 根据机构ID查询公示信息
     *
     * @param institutionId 机构ID
     * @return 养老机构公示信息
     */
    public PensionInstitutionPublic selectPensionInstitutionPublicByInstitutionId(Long institutionId);

    /**
     * 查询养老机构公示信息列表(关联查询机构信息)
     *
     * @param pensionInstitutionPublic 养老机构公示信息
     * @return 养老机构公示信息集合
     */
    public List<PensionInstitutionPublic> selectPensionInstitutionPublicList(PensionInstitutionPublic pensionInstitutionPublic);

    /**
     * 新增养老机构公示信息
     *
     * @param pensionInstitutionPublic 养老机构公示信息
     * @return 结果
     */
    public int insertPensionInstitutionPublic(PensionInstitutionPublic pensionInstitutionPublic);

    /**
     * 修改养老机构公示信息
     *
     * @param pensionInstitutionPublic 养老机构公示信息
     * @return 结果
     */
    public int updatePensionInstitutionPublic(PensionInstitutionPublic pensionInstitutionPublic);

    /**
     * 删除养老机构公示信息
     *
     * @param publicId 养老机构公示信息主键
     * @return 结果
     */
    public int deletePensionInstitutionPublicByPublicId(Long publicId);

    /**
     * 批量删除养老机构公示信息
     *
     * @param publicIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePensionInstitutionPublicByPublicIds(Long[] publicIds);

    /**
     * 发布公示
     *
     * @param publicId 公示ID
     * @return 结果
     */
    public int publishPublicity(Long publicId);

    /**
     * 取消公示
     *
     * @param publicId 公示ID
     * @return 结果
     */
    public int unpublishPublicity(Long publicId);
}
