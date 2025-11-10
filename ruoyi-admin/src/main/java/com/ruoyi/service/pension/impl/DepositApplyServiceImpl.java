package com.ruoyi.service.pension.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.DepositApplyMapper;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.service.pension.IDepositApplyService;

/**
 * 押金使用申请Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class DepositApplyServiceImpl implements IDepositApplyService
{
    @Autowired
    private DepositApplyMapper depositApplyMapper;

    /**
     * 查询押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 押金使用申请
     */
    @Override
    public DepositApply selectDepositApplyByApplyId(Long applyId)
    {
        return depositApplyMapper.selectDepositApplyByApplyId(applyId);
    }

    /**
     * 查询押金使用申请列表
     *
     * @param depositApply 押金使用申请
     * @return 押金使用申请
     */
    @Override
    public List<DepositApply> selectDepositApplyList(DepositApply depositApply)
    {
        return depositApplyMapper.selectDepositApplyList(depositApply);
    }

    /**
     * 根据老人ID查询押金使用申请列表
     *
     * @param elderId 老人ID
     * @return 押金使用申请集合
     */
    @Override
    public List<DepositApply> selectDepositApplyByElderId(Long elderId)
    {
        return depositApplyMapper.selectDepositApplyByElderId(elderId);
    }

    /**
     * 根据机构ID查询押金使用申请列表
     *
     * @param institutionId 机构ID
     * @return 押金使用申请集合
     */
    @Override
    public List<DepositApply> selectDepositApplyByInstitutionId(Long institutionId)
    {
        return depositApplyMapper.selectDepositApplyByInstitutionId(institutionId);
    }

    /**
     * 新增押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    @Override
    public int insertDepositApply(DepositApply depositApply)
    {
        depositApply.setCreateTime(DateUtils.getNowDate());
        return depositApplyMapper.insertDepositApply(depositApply);
    }

    /**
     * 修改押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    @Override
    public int updateDepositApply(DepositApply depositApply)
    {
        depositApply.setUpdateTime(DateUtils.getNowDate());
        return depositApplyMapper.updateDepositApply(depositApply);
    }

    /**
     * 批量删除押金使用申请
     *
     * @param applyIds 需要删除的押金使用申请主键
     * @return 结果
     */
    @Override
    public int deleteDepositApplyByApplyIds(Long[] applyIds)
    {
        return depositApplyMapper.deleteDepositApplyByApplyIds(applyIds);
    }

    /**
     * 删除押金使用申请信息
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    @Override
    public int deleteDepositApplyByApplyId(Long applyId)
    {
        return depositApplyMapper.deleteDepositApplyByApplyId(applyId);
    }
}
