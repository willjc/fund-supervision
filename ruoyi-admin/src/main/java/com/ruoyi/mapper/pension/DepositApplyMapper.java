package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.DepositApply;
import org.apache.ibatis.annotations.Param;

/**
 * 押金使用申请Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface DepositApplyMapper
{
    /**
     * 查询押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 押金使用申请
     */
    public DepositApply selectDepositApplyByApplyId(Long applyId);

    /**
     * 查询押金使用申请列表
     *
     * @param depositApply 押金使用申请
     * @return 押金使用申请集合
     */
    public List<DepositApply> selectDepositApplyList(DepositApply depositApply);

    /**
     * 根据老人ID查询押金使用申请列表
     *
     * @param elderId 老人ID
     * @return 押金使用申请集合
     */
    public List<DepositApply> selectDepositApplyByElderId(@Param("elderId") Long elderId);

    /**
     * 根据机构ID查询押金使用申请列表
     *
     * @param institutionId 机构ID
     * @return 押金使用申请集合
     */
    public List<DepositApply> selectDepositApplyByInstitutionId(@Param("institutionId") Long institutionId);

    /**
     * 新增押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    public int insertDepositApply(DepositApply depositApply);

    /**
     * 修改押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    public int updateDepositApply(DepositApply depositApply);

    /**
     * 删除押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    public int deleteDepositApplyByApplyId(Long applyId);

    /**
     * 批量删除押金使用申请
     *
     * @param applyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDepositApplyByApplyIds(Long[] applyIds);
}
