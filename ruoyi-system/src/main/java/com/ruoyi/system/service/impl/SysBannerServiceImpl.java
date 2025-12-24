package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysBanner;
import com.ruoyi.system.mapper.SysBannerMapper;
import com.ruoyi.system.service.ISysBannerService;

/**
 * 幻灯片管理 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysBannerServiceImpl implements ISysBannerService
{
    @Autowired
    private SysBannerMapper bannerMapper;

    /**
     * 查询幻灯片信息
     *
     * @param bannerId 幻灯片ID
     * @return 幻灯片信息
     */
    @Override
    public SysBanner selectBannerById(Long bannerId)
    {
        return bannerMapper.selectBannerById(bannerId);
    }

    /**
     * 查询幻灯片列表
     *
     * @param banner 幻灯片信息
     * @return 幻灯片集合
     */
    @Override
    public List<SysBanner> selectBannerList(SysBanner banner)
    {
        return bannerMapper.selectBannerList(banner);
    }

    /**
     * 新增幻灯片
     *
     * @param banner 幻灯片信息
     * @return 结果
     */
    @Override
    public int insertBanner(SysBanner banner)
    {
        return bannerMapper.insertBanner(banner);
    }

    /**
     * 修改幻灯片
     *
     * @param banner 幻灯片信息
     * @return 结果
     */
    @Override
    public int updateBanner(SysBanner banner)
    {
        return bannerMapper.updateBanner(banner);
    }

    /**
     * 删除幻灯片对象
     *
     * @param bannerId 幻灯片ID
     * @return 结果
     */
    @Override
    public int deleteBannerById(Long bannerId)
    {
        return bannerMapper.deleteBannerById(bannerId);
    }

    /**
     * 批量删除幻灯片信息
     *
     * @param bannerIds 需要删除的幻灯片ID
     * @return 结果
     */
    @Override
    public int deleteBannerByIds(Long[] bannerIds)
    {
        return bannerMapper.deleteBannerByIds(bannerIds);
    }
}
