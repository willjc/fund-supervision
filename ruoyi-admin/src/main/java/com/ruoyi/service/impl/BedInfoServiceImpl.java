package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.BedInfoMapper;
import com.ruoyi.domain.BedInfo;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.common.exception.ServiceException;

/**
 * 床位信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class BedInfoServiceImpl implements IBedInfoService
{
    @Autowired
    private BedInfoMapper bedInfoMapper;

    /**
     * 查询床位信息
     *
     * @param bedId 床位信息主键
     * @return 床位信息
     */
    @Override
    public BedInfo selectBedInfoByBedId(Long bedId)
    {
        return bedInfoMapper.selectBedInfoByBedId(bedId);
    }

    /**
     * 查询床位信息列表
     *
     * @param bedInfo 床位信息
     * @return 床位信息
     */
    @Override
    public List<BedInfo> selectBedInfoList(BedInfo bedInfo)
    {
        return bedInfoMapper.selectBedInfoList(bedInfo);
    }

    /**
     * 查询可用床位列表
     *
     * @param institutionId 机构ID
     * @return 可用床位集合
     */
    @Override
    public List<BedInfo> selectAvailableBeds(Long institutionId)
    {
        return bedInfoMapper.selectAvailableBeds(institutionId);
    }

    /**
     * 新增床位信息
     *
     * @param bedInfo 床位信息
     * @return 结果
     */
    @Override
    public int insertBedInfo(BedInfo bedInfo)
    {
        bedInfo.setCreateTime(DateUtils.getNowDate());
        bedInfo.setBedStatus("0"); // 默认状态为空置
        return bedInfoMapper.insertBedInfo(bedInfo);
    }

    /**
     * 修改床位信息
     *
     * @param bedInfo 床位信息
     * @return 结果
     */
    @Override
    public int updateBedInfo(BedInfo bedInfo)
    {
        bedInfo.setUpdateTime(DateUtils.getNowDate());
        return bedInfoMapper.updateBedInfo(bedInfo);
    }

    /**
     * 批量删除床位信息
     *
     * @param bedIds 需要删除的床位信息主键
     * @return 结果
     */
    @Override
    public int deleteBedInfoByBedIds(Long[] bedIds)
    {
        return bedInfoMapper.deleteBedInfoByBedIds(bedIds);
    }

    /**
     * 删除床位信息信息
     *
     * @param bedId 床位信息主键
     * @return 结果
     */
    @Override
    public int deleteBedInfoByBedId(Long bedId)
    {
        return bedInfoMapper.deleteBedInfoByBedId(bedId);
    }

    /**
     * 导出床位信息
     *
     * @param bedInfo 查询条件
     * @return 床位信息列表
     */
    @Override
    public List<BedInfo> exportBedInfo(BedInfo bedInfo)
    {
        return bedInfoMapper.selectBedInfoList(bedInfo);
    }

    /**
     * 批量导入床位信息
     *
     * @param bedList 床位信息列表
     * @param updateSupport 是否更新已存在的床位
     * @return 导入结果消息
     */
    @Override
    public String importBedInfo(List<BedInfo> bedList, boolean updateSupport)
    {
        if (StringUtils.isNull(bedList) || bedList.size() == 0)
        {
            throw new ServiceException("导入床位数据不能为空!");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (BedInfo bedInfo : bedList)
        {
            try
            {
                // 验证必填字段
                if (StringUtils.isEmpty(bedInfo.getRoomNumber()))
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房间号不能为空");
                    continue;
                }
                if (StringUtils.isEmpty(bedInfo.getBedNumber()))
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、床位号不能为空");
                    continue;
                }
                if (bedInfo.getInstitutionId() == null)
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、机构ID不能为空 (房间号: ").append(bedInfo.getRoomNumber()).append(", 床位号: ").append(bedInfo.getBedNumber()).append(")");
                    continue;
                }

                // 查询是否存在相同的床位(同一机构下,房间号和床位号相同)
                BedInfo existBed = bedInfoMapper.selectBedByRoomAndBedNumber(
                    bedInfo.getInstitutionId(),
                    bedInfo.getRoomNumber(),
                    bedInfo.getBedNumber()
                );

                if (StringUtils.isNull(existBed))
                {
                    // 新增床位
                    bedInfo.setCreateTime(DateUtils.getNowDate());
                    if (StringUtils.isEmpty(bedInfo.getBedStatus()))
                    {
                        bedInfo.setBedStatus("0"); // 默认状态为空置
                    }
                    this.insertBedInfo(bedInfo);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、床位 ").append(bedInfo.getRoomNumber()).append("-").append(bedInfo.getBedNumber()).append(" 导入成功");
                }
                else if (updateSupport)
                {
                    // 更新床位
                    bedInfo.setBedId(existBed.getBedId());
                    bedInfo.setUpdateTime(DateUtils.getNowDate());
                    this.updateBedInfo(bedInfo);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、床位 ").append(bedInfo.getRoomNumber()).append("-").append(bedInfo.getBedNumber()).append(" 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、床位 ").append(bedInfo.getRoomNumber()).append("-").append(bedInfo.getBedNumber()).append(" 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、床位 " + bedInfo.getRoomNumber() + "-" + bedInfo.getBedNumber() + " 导入失败: ";
                failureMsg.append(msg).append(e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉,导入失败!共 " + failureNum + " 条数据格式不正确,错误如下:");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您,数据已全部导入成功!共 " + successNum + " 条,数据如下:");
        }
        return successMsg.toString();
    }
}