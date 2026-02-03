package com.ruoyi.service.impl.pension;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.SupervisionWarningMapper;
import com.ruoyi.domain.pension.SupervisionWarning;
import com.ruoyi.service.pension.ISupervisionWarningService;

/**
 * 预警信息Service业务层处理
 *
 * @author ruoyi
 * @date 2026-02-03
 */
@Service
public class SupervisionWarningServiceImpl implements ISupervisionWarningService
{
    @Autowired
    private SupervisionWarningMapper supervisionWarningMapper;

    /**
     * 查询预警信息
     *
     * @param warningId 预警信息主键
     * @return 预警信息
     */
    @Override
    public SupervisionWarning selectSupervisionWarningByWarningId(Long warningId)
    {
        return supervisionWarningMapper.selectSupervisionWarningByWarningId(warningId);
    }

    /**
     * 根据预警编号查询预警信息
     *
     * @param warningNo 预警编号
     * @return 预警信息
     */
    @Override
    public SupervisionWarning selectSupervisionWarningByWarningNo(String warningNo)
    {
        return supervisionWarningMapper.selectSupervisionWarningByWarningNo(warningNo);
    }

    /**
     * 查询预警信息列表
     *
     * @param supervisionWarning 预警信息
     * @return 预警信息
     */
    @Override
    public List<SupervisionWarning> selectSupervisionWarningList(SupervisionWarning supervisionWarning)
    {
        return supervisionWarningMapper.selectSupervisionWarningList(supervisionWarning);
    }

    /**
     * 新增预警信息
     *
     * @param supervisionWarning 预警信息
     * @return 结果
     */
    @Override
    public int insertSupervisionWarning(SupervisionWarning supervisionWarning)
    {
        return supervisionWarningMapper.insertSupervisionWarning(supervisionWarning);
    }

    /**
     * 修改预警信息
     *
     * @param supervisionWarning 预警信息
     * @return 结果
     */
    @Override
    public int updateSupervisionWarning(SupervisionWarning supervisionWarning)
    {
        return supervisionWarningMapper.updateSupervisionWarning(supervisionWarning);
    }

    /**
     * 批量删除预警信息
     *
     * @param warningIds 需要删除的预警信息主键
     * @return 结果
     */
    @Override
    public int deleteSupervisionWarningByWarningIds(Long[] warningIds)
    {
        return supervisionWarningMapper.deleteSupervisionWarningByWarningIds(warningIds);
    }

    /**
     * 删除预警信息信息
     *
     * @param warningId 预警信息主键
     * @return 结果
     */
    @Override
    public int deleteSupervisionWarningByWarningId(Long warningId)
    {
        return supervisionWarningMapper.deleteSupervisionWarningByWarningId(warningId);
    }

    /**
     * 处理预警
     *
     * @param warningNo 预警编号
     * @param handler 处理人
     * @param handleRemark 处理备注
     * @return 结果
     */
    @Override
    public int handleWarning(String warningNo, String handler, String handleRemark)
    {
        SupervisionWarning warning = supervisionWarningMapper.selectSupervisionWarningByWarningNo(warningNo);
        if (warning == null)
        {
            return 0;
        }
        warning.setWarningStatus("1");
        warning.setHandler(handler);
        warning.setHandleTime(new Date());
        warning.setHandleRemark(handleRemark);
        return supervisionWarningMapper.updateSupervisionWarning(warning);
    }
}
