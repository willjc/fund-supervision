package com.ruoyi.web.controller.h5;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.UserFavorite;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IUserFavoriteService;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.PensionInstitutionPublic;

/**
 * 用户机构收藏Controller (H5端)
 *
 * @author ruoyi
 * @date 2024-12-09
 */
@RestController
@RequestMapping("/h5/favorite")
public class UserFavoriteController extends BaseController
{
    @Autowired
    private IUserFavoriteService userFavoriteService;

    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    @Autowired
    private IPensionInstitutionPublicService pensionInstitutionPublicService;

    /**
     * 查询用户机构收藏列表
     */
    @GetMapping("/list")
    public TableDataInfo list(UserFavorite userFavorite)
    {
        startPage();
        // 只能查询当前登录用户的收藏记录
        Long userId = SecurityUtils.getUserId();
        userFavorite.setUserId(userId);
        List<UserFavorite> list = userFavoriteService.selectUserFavoriteList(userFavorite);

        // 获取机构信息并合并数据
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (UserFavorite favorite : list) {
            Map<String, Object> item = new HashMap<>();

            // 添加收藏信息
            item.put("favoriteId", favorite.getFavoriteId());
            item.put("institutionId", favorite.getInstitutionId());
            item.put("createTime", favorite.getCreateTime());
            item.put("userId", favorite.getUserId());

            // 获取机构详细信息
            try {
                PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(favorite.getInstitutionId());
                if (institution != null) {
                    item.put("institutionName", institution.getInstitutionName());
                    item.put("address", institution.getActualAddress());
                    item.put("contactPhone", institution.getContactPhone());
                    item.put("bedCount", institution.getBedCount());
                    item.put("institutionType", institution.getInstitutionType());

                    // 优先从公示信息获取主图
                    String coverImage = null;
                    try {
                        PensionInstitutionPublic publicity = pensionInstitutionPublicService.selectPensionInstitutionPublicByInstitutionId(favorite.getInstitutionId());
                        if (publicity != null) {
                            // 首先使用主图
                            if (publicity.getMainPicture() != null && !publicity.getMainPicture().trim().isEmpty()) {
                                coverImage = publicity.getMainPicture();
                                System.out.println("从公示信息获取主图: " + coverImage);
                            }
                            // 如果没有主图，尝试使用环境图片
                            else if (publicity.getEnvironmentImgs() != null && !publicity.getEnvironmentImgs().trim().isEmpty()) {
                                try {
                                    String[] imageUrls = publicity.getEnvironmentImgs().split(",");
                                    if (imageUrls.length > 0) {
                                        coverImage = imageUrls[0].trim();
                                        System.out.println("从环境图片获取封面: " + coverImage);
                                    }
                                } catch (Exception e) {
                                    System.out.println("环境图片解析异常: " + e.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("查询公示信息异常: " + e.getMessage());
                    }

                    // 如果公示信息没有图片，尝试从基础机构信息获取
                    if (coverImage == null) {
                        String coverImages = institution.getCoverImages();
                        System.out.println("机构ID: " + favorite.getInstitutionId() + ", 原始coverImages: " + coverImages);

                        if (coverImages != null && !coverImages.trim().isEmpty()) {
                            try {
                                // 如果是逗号分隔的多张图片，取第一张
                                String[] imageUrls = coverImages.split(",");
                                if (imageUrls.length > 0) {
                                    coverImage = imageUrls[0].trim();
                                    System.out.println("从基础信息获取图片: " + coverImage);
                                }
                            } catch (Exception e) {
                                System.out.println("图片解析异常: " + e.getMessage());
                            }
                        }
                    }

                    // 如果还是没有图片，使用默认图片
                    if (coverImage == null || coverImage.trim().isEmpty()) {
                        coverImage = "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg";
                        System.out.println("使用默认图片: " + coverImage);
                    }

                    System.out.println("最终使用的coverImage: " + coverImage);
                    item.put("coverImage", coverImage);

                    // 价格信息暂时使用默认值
                    item.put("price", 2000);
                } else {
                    // 机构不存在时的默认值
                    item.put("institutionName", "机构ID: " + favorite.getInstitutionId());
                    item.put("address", "地址信息完善中");
                    item.put("contactPhone", "");
                    item.put("bedCount", 0);
                    item.put("institutionType", "养老机构");
                    item.put("coverImage", "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg");
                    item.put("price", 2000);
                }
            } catch (Exception e) {
                // 异常时使用默认值
                item.put("institutionName", "机构ID: " + favorite.getInstitutionId());
                item.put("address", "地址信息完善中");
                item.put("contactPhone", "");
                item.put("bedCount", 0);
                item.put("institutionType", "养老机构");
                item.put("coverImage", "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg");
                item.put("price", 2000);
            }

            resultList.add(item);
        }

        return getDataTable(resultList);
    }

    /**
     * 获取用户机构收藏详细信息
     */
    @GetMapping(value = "/{favoriteId}")
    public AjaxResult getInfo(@PathVariable("favoriteId") Long favoriteId)
    {
        return success(userFavoriteService.selectUserFavoriteByFavoriteId(favoriteId));
    }

    /**
     * 检查用户是否已收藏机构
     */
    @GetMapping("/check/{institutionId}")
    public AjaxResult checkFavorite(@PathVariable("institutionId") Long institutionId)
    {
        Long userId = SecurityUtils.getUserId();
        System.out.println("检查收藏状态 - userId: " + userId + ", institutionId: " + institutionId);
        boolean isFavorited = userFavoriteService.checkUserFavorite(userId, institutionId);
        System.out.println("收藏状态结果: " + isFavorited);
        return AjaxResult.success().put("isFavorited", isFavorited);
    }

    /**
     * 收藏机构
     */
    @PostMapping("/add/{institutionId}")
    @Log(title = "收藏机构", businessType = BusinessType.INSERT)
    public AjaxResult addFavorite(@PathVariable("institutionId") Long institutionId)
    {
        Long userId = SecurityUtils.getUserId();
        System.out.println("收藏机构 - userId: " + userId + ", institutionId: " + institutionId);

        // 检查是否已收藏
        boolean isFavorited = userFavoriteService.checkUserFavorite(userId, institutionId);
        System.out.println("检查是否已收藏: " + isFavorited);

        if (isFavorited) {
            return AjaxResult.error("您已经收藏过该机构");
        }

        try {
            int result = userFavoriteService.userFavoriteInstitution(userId, institutionId);
            System.out.println("收藏操作结果: " + result);
            return result > 0 ? AjaxResult.success("收藏成功") : AjaxResult.error("收藏失败");
        } catch (Exception e) {
            System.out.println("收藏操作异常: " + e.getMessage());
            e.printStackTrace();
            return AjaxResult.error("收藏失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏机构
     */
    @DeleteMapping("/remove/{institutionId}")
    @Log(title = "取消收藏机构", businessType = BusinessType.DELETE)
    public AjaxResult removeFavorite(@PathVariable("institutionId") Long institutionId)
    {
        Long userId = SecurityUtils.getUserId();
        // 检查是否已收藏
        if (!userFavoriteService.checkUserFavorite(userId, institutionId)) {
            return AjaxResult.error("您尚未收藏该机构");
        }

        int result = userFavoriteService.userUnfavoriteInstitution(userId, institutionId);
        return result > 0 ? AjaxResult.success("取消收藏成功") : AjaxResult.error("取消收藏失败");
    }

    /**
     * 批量删除用户机构收藏
     */
    @Log(title = "用户机构收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/{favoriteIds}")
    public AjaxResult remove(@PathVariable Long[] favoriteIds)
    {
        return toAjax(userFavoriteService.deleteUserFavoriteByFavoriteIds(favoriteIds));
    }
}