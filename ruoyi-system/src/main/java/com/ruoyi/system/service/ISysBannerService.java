package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysBanner;

/**
 * 幻灯片管理 服务层
 *
 * @author ruoyi
 */
public interface ISysBannerService
{
    /**
     * 查询幻灯片信息
     *
     * @param bannerId 幻灯片ID
     * @return 幻灯片信息
     */
    public SysBanner selectBannerById(Long bannerId);

    /**
     * 查询幻灯片列表
     *
     * @param banner 幻灯片信息
     * @return 幻灯片集合
     */
    public List<SysBanner> selectBannerList(SysBanner banner);

    /**
     * 新增幻灯片
     *
     * @param banner 幻灯片信息
     * @return 结果
     */
    public int insertBanner(SysBanner banner);

    /**
     * 修改幻灯片
     *
     * @param banner 幻灯片信息
     * @return 结果
     */
    public int updateBanner(SysBanner banner);

    /**
     * 删除幻灯片信息
     *
     * @param bannerId 幻灯片ID
     * @return 结果
     */
    public int deleteBannerById(Long bannerId);

    /**
     * 批量删除幻灯片信息
     *
     * @param bannerIds 需要删除的幻灯片ID
     * @return 结果
     */
    public int deleteBannerByIds(Long[] bannerIds);
}
