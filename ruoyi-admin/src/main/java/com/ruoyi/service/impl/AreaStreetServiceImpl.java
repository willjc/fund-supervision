package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AreaStreetMapper;
import com.ruoyi.domain.AreaStreet;
import com.ruoyi.service.IAreaStreetService;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.StringUtils;

/**
 * 区域街道信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-04
 */
@Service
public class AreaStreetServiceImpl implements IAreaStreetService
{
    @Autowired
    private AreaStreetMapper areaStreetMapper;

    /**
     * 查询区域街道信息
     *
     * @param id 区域街道信息主键
     * @return 区域街道信息
     */
    @Override
    public AreaStreet selectAreaStreetById(Long id)
    {
        return areaStreetMapper.selectAreaStreetById(id);
    }

    /**
     * 查询区域街道信息列表
     *
     * @param areaStreet 区域街道信息
     * @return 区域街道信息
     */
    @Override
    public List<AreaStreet> selectAreaStreetList(AreaStreet areaStreet)
    {
        return areaStreetMapper.selectAreaStreetList(areaStreet);
    }

    /**
     * 查询所有区域列表
     *
     * @return 区域列表
     */
    @Override
    public List<AreaStreet> selectAllAreas()
    {
        return areaStreetMapper.selectAllAreas();
    }

    /**
     * 根据区域代码查询街道列表
     *
     * @param areaCode 区域代码
     * @return 街道列表
     */
    @Override
    public List<AreaStreet> selectStreetsByAreaCode(String areaCode)
    {
        return areaStreetMapper.selectStreetsByAreaCode(areaCode);
    }

    /**
     * 新增区域街道信息
     *
     * @param areaStreet 区域街道信息
     * @return 结果
     */
    @Override
    public int insertAreaStreet(AreaStreet areaStreet)
    {
        areaStreet.setCreateTime(DateUtils.getNowDate());
        return areaStreetMapper.insertAreaStreet(areaStreet);
    }

    /**
     * 修改区域街道信息
     *
     * @param areaStreet 区域街道信息
     * @return 结果
     */
    @Override
    public int updateAreaStreet(AreaStreet areaStreet)
    {
        areaStreet.setUpdateTime(DateUtils.getNowDate());
        return areaStreetMapper.updateAreaStreet(areaStreet);
    }

    /**
     * 批量删除区域街道信息
     *
     * @param ids 需要删除的区域街道信息主键
     * @return 结果
     */
    @Override
    public int deleteAreaStreetByIds(Long[] ids)
    {
        return areaStreetMapper.deleteAreaStreetByIds(ids);
    }

    /**
     * 删除区域街道信息信息
     *
     * @param id 区域街道信息主键
     * @return 结果
     */
    @Override
    public int deleteAreaStreetById(Long id)
    {
        return areaStreetMapper.deleteAreaStreetById(id);
    }

    /**
     * 批量导入区域街道数据
     *
     * @param areaStreetList 区域街道信息列表
     * @return 导入结果
     */
    @Override
    public String importAreaStreet(List<AreaStreet> areaStreetList)
    {
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (AreaStreet areaStreet : areaStreetList)
        {
            try
            {
                // 检查是否已存在
                List<AreaStreet> existingStreets = areaStreetMapper.selectStreetsByAreaCode(areaStreet.getAreaCode());
                boolean exists = false;
                for (AreaStreet existing : existingStreets)
                {
                    if (existing.getStreetName().equals(areaStreet.getStreetName()))
                    {
                        exists = true;
                        break;
                    }
                }

                if (!exists)
                {
                    this.insertAreaStreet(areaStreet);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、区域 " + areaStreet.getAreaName() + " - 街道 " + areaStreet.getStreetName() + " 导入成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、区域 " + areaStreet.getAreaName() + " - 街道 " + areaStreet.getStreetName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、区域 " + areaStreet.getAreaName() + " - 街道 " + areaStreet.getStreetName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }

        return successMsg.toString();
    }
}