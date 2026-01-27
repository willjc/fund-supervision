package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.UserFavorite;

/**
 * 用户机构收藏Service接口
 *
 * @author ruoyi
 * @date 2024-12-09
 */
public interface IUserFavoriteService
{
    /**
     * 查询用户机构收藏
     *
     * @param favoriteId 用户机构收藏主键
     * @return 用户机构收藏
     */
    public UserFavorite selectUserFavoriteByFavoriteId(Long favoriteId);

    /**
     * 查询用户机构收藏列表
     *
     * @param userFavorite 用户机构收藏
     * @return 用户机构收藏集合
     */
    public List<UserFavorite> selectUserFavoriteList(UserFavorite userFavorite);

    /**
     * 新增用户机构收藏
     *
     * @param userFavorite 用户机构收藏
     * @return 结果
     */
    public int insertUserFavorite(UserFavorite userFavorite);

    /**
     * 修改用户机构收藏
     *
     * @param userFavorite 用户机构收藏
     * @return 结果
     */
    public int updateUserFavorite(UserFavorite userFavorite);

    /**
     * 批量删除用户机构收藏
     *
     * @param favoriteIds 需要删除的用户机构收藏主键集合
     * @return 结果
     */
    public int deleteUserFavoriteByFavoriteIds(Long[] favoriteIds);

    /**
     * 删除用户机构收藏信息
     *
     * @param favoriteId 用户机构收藏主键
     * @return 结果
     */
    public int deleteUserFavoriteByFavoriteId(Long favoriteId);

    /**
     * 检查用户是否已收藏机构
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return true-已收藏 false-未收藏
     */
    public boolean checkUserFavorite(Long userId, Long institutionId);

    /**
     * 用户收藏机构
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return 结果
     */
    public int userFavoriteInstitution(Long userId, Long institutionId);

    /**
     * 用户取消收藏机构
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return 结果
     */
    public int userUnfavoriteInstitution(Long userId, Long institutionId);

    /**
     * 获取用户的收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    public int getUserFavoriteCount(Long userId);
}