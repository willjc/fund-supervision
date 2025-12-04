package com.ruoyi.service.impl;

import java.util.List;
import java.util.Map;
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
     * @param institutionId 机构ID
     * @param updateSupport 是否更新已存在的床位
     * @return 导入结果消息
     */
    @Override
    public String importBedInfo(List<BedInfo> bedList, Long institutionId, boolean updateSupport)
    {
        if (StringUtils.isNull(bedList) || bedList.size() == 0)
        {
            throw new ServiceException("导入床位数据不能为空!");
        }
        if (institutionId == null)
        {
            throw new ServiceException("请选择要导入床位的机构!");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (BedInfo bedInfo : bedList)
        {
            try
            {
                // 设置机构ID
                bedInfo.setInstitutionId(institutionId);

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

                // 转换中文值为数字代码
                convertChineseToCode(bedInfo);

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

    /**
     * 转换中文值为数字代码
     *
     * @param bedInfo 床位信息
     */
    private void convertChineseToCode(BedInfo bedInfo)
    {
        // 转换床位类型
        if (StringUtils.isNotEmpty(bedInfo.getBedType()))
        {
            switch (bedInfo.getBedType().trim())
            {
                case "普通床位":
                    bedInfo.setBedType("1");
                    break;
                case "豪华床位":
                    bedInfo.setBedType("2");
                    break;
                case "医疗床位":
                    bedInfo.setBedType("3");
                    break;
                // 如果已经是数字,保持不变
                case "1":
                case "2":
                case "3":
                    break;
                default:
                    throw new ServiceException("床位类型格式错误,请填写: 普通床位/豪华床位/医疗床位");
            }
        }

        // 转换床位状态
        if (StringUtils.isNotEmpty(bedInfo.getBedStatus()))
        {
            switch (bedInfo.getBedStatus().trim())
            {
                case "空置":
                    bedInfo.setBedStatus("0");
                    break;
                case "占用":
                    bedInfo.setBedStatus("1");
                    break;
                case "维修":
                    bedInfo.setBedStatus("2");
                    break;
                // 如果已经是数字,保持不变
                case "0":
                case "1":
                case "2":
                    break;
                default:
                    throw new ServiceException("床位状态格式错误,请填写: 空置/占用/维修");
            }
        }

        // 转换独立卫浴
        if (StringUtils.isNotEmpty(bedInfo.getHasBathroom()))
        {
            switch (bedInfo.getHasBathroom().trim())
            {
                case "是":
                    bedInfo.setHasBathroom("1");
                    break;
                case "否":
                    bedInfo.setHasBathroom("0");
                    break;
                // 如果已经是数字,保持不变
                case "0":
                case "1":
                    break;
                default:
                    throw new ServiceException("独立卫浴格式错误,请填写: 是/否");
            }
        }

        // 转换阳台
        if (StringUtils.isNotEmpty(bedInfo.getHasBalcony()))
        {
            switch (bedInfo.getHasBalcony().trim())
            {
                case "是":
                    bedInfo.setHasBalcony("1");
                    break;
                case "否":
                    bedInfo.setHasBalcony("0");
                    break;
                // 如果已经是数字,保持不变
                case "0":
                case "1":
                    break;
                default:
                    throw new ServiceException("阳台格式错误,请填写: 是/否");
            }
        }
    }

    /**
     * 获取机构床位统计信息
     *
     * @param institutionId 机构ID
     * @return 床位统计信息 {totalBeds: 总床位, availableBeds: 可定床位}
     */
    @Override
    public Map<String, Object> getBedStatistics(Long institutionId)
    {
        return bedInfoMapper.selectBedStatistics(institutionId);
    }
}