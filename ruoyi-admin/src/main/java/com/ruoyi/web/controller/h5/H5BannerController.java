package com.ruoyi.web.controller.h5;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysBanner;
import com.ruoyi.system.service.ISysBannerService;

/**
 * H5幻灯片Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/banner")
public class H5BannerController extends BaseController
{
    @Autowired
    private ISysBannerService bannerService;

    /**
     * 获取幻灯片列表（H5端）
     * @param position 位置：1-首页 2-机构页
     * @return 幻灯片列表
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) Integer position)
    {
        try
        {
            SysBanner query = new SysBanner();
            // 只查询启用状态的幻灯片
            query.setStatus("0"); // 0-正常 1-停用
            if (position != null)
            {
                query.setPosition(String.valueOf(position));
            }

            List<SysBanner> bannerList = bannerService.selectBannerList(query);

            // 按sort排序
            bannerList.sort((a, b) -> {
                if (a.getSort() == null) return 1;
                if (b.getSort() == null) return -1;
                return a.getSort().compareTo(b.getSort());
            });

            // 转换为前端期望的格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (SysBanner banner : bannerList)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("id", banner.getBannerId());
                item.put("imageUrl", banner.getImageUrl());
                item.put("title", banner.getTitle());
                item.put("linkType", banner.getLinkType());
                item.put("linkValue", banner.getLinkValue());
                result.add(item);
            }

            return success(result);
        }
        catch (Exception e)
        {
            logger.error("获取幻灯片列表失败", e);
            return error("获取幻灯片列表失败");
        }
    }
}
