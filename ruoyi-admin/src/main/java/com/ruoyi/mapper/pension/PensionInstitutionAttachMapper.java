package com.ruoyi.mapper.pension;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.domain.pension.PensionInstitutionAttach;

/**
 * 机构附件Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-25
 */
public interface PensionInstitutionAttachMapper
{
    /**
     * 查询机构附件
     *
     * @param attachId 机构附件主键
     * @return 机构附件
     */
    public PensionInstitutionAttach selectPensionInstitutionAttachByAttachId(Long attachId);

    /**
     * 查询机构附件列表
     *
     * @param pensionInstitutionAttach 机构附件
     * @return 机构附件集合
     */
    public List<PensionInstitutionAttach> selectPensionInstitutionAttachList(PensionInstitutionAttach pensionInstitutionAttach);

    /**
     * 根据机构ID和附件类型查询附件列表
     *
     * @param institutionId 机构ID
     * @param attachType 附件类型
     * @return 附件列表
     */
    public List<PensionInstitutionAttach> selectByInstitutionIdAndType(@Param("institutionId") Long institutionId, @Param("attachType") String attachType);

    /**
     * 根据机构ID查询所有附件（按类型分组）
     *
     * @param institutionId 机构ID
     * @return 附件分类列表
     */
    public List<Map<String, Object>> selectAttachmentsGroupByType(@Param("institutionId") Long institutionId);

    /**
     * 新增机构附件
     *
     * @param pensionInstitutionAttach 机构附件
     * @return 结果
     */
    public int insertPensionInstitutionAttach(PensionInstitutionAttach pensionInstitutionAttach);

    /**
     * 修改机构附件
     *
     * @param pensionInstitutionAttach 机构附件
     * @return 结果
     */
    public int updatePensionInstitutionAttach(PensionInstitutionAttach pensionInstitutionAttach);

    /**
     * 删除机构附件
     *
     * @param attachId 机构附件主键
     * @return 结果
     */
    public int deletePensionInstitutionAttachByAttachId(Long attachId);

    /**
     * 批量删除机构附件
     *
     * @param attachIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePensionInstitutionAttachByAttachIds(Long[] attachIds);

    /**
     * 根据机构ID删除该机构的所有附件
     *
     * @param institutionId 机构ID
     * @return 结果
     */
    public int deletePensionInstitutionAttachByInstitutionId(Long institutionId);
}
