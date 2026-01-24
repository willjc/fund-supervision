package com.ruoyi.web.controller.h5.vo;

import java.util.List;

/**
 * 机构图片分类VO
 *
 * @author ruoyi
 * @date 2025-01-25
 */
public class InstitutionImageCategoryVO
{
    /** 分类标识 (vr, main, environment, room, basic, park) */
    private String key;

    /** 分类名称 (VR, 主图, 环境图片, 房间图片, 基础设施, 园址设施) */
    private String name;

    /** 图片URL列表 */
    private List<String> images;

    public InstitutionImageCategoryVO()
    {
    }

    public InstitutionImageCategoryVO(String key, String name, List<String> images)
    {
        this.key = key;
        this.name = name;
        this.images = images;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setImages(List<String> images)
    {
        this.images = images;
    }

    public List<String> getImages()
    {
        return images;
    }

    /**
     * 判断是否有图片
     */
    public boolean hasImages()
    {
        return images != null && !images.isEmpty();
    }

    /**
     * 获取第一张图片
     */
    public String getFirstImage()
    {
        if (hasImages())
        {
            return images.get(0);
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "InstitutionImageCategoryVO{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", images=" + images +
                '}';
    }
}
