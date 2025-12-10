package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.UserFavoriteMapper;
import com.ruoyi.domain.UserFavorite;
import com.ruoyi.service.IUserFavoriteService;

/**
 * 用户机构收藏Service业务层处理
 *
 * @author ruoyi
 * @date 2024-12-09
 */
@Service
public class UserFavoriteServiceImpl implements IUserFavoriteService
{
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    /**
     * 查询用户机构收藏
     *
     * @param favoriteId 用户机构收藏主键
     * @return 用户机构收藏
     */
    @Override
    public UserFavorite selectUserFavoriteByFavoriteId(Long favoriteId)
    {
        return userFavoriteMapper.selectUserFavoriteByFavoriteId(favoriteId);
    }

    /**
     * 查询用户机构收藏列表
     *
     * @param userFavorite 用户机构收藏
     * @return 用户机构收藏
     */
    @Override
    public List<UserFavorite> selectUserFavoriteList(UserFavorite userFavorite)
    {
        return userFavoriteMapper.selectUserFavoriteList(userFavorite);
    }

    /**
     * 新增用户机构收藏
     *
     * @param userFavorite 用户机构收藏
     * @return 结果
     */
    @Override
    public int insertUserFavorite(UserFavorite userFavorite)
    {
        userFavorite.setCreateTime(DateUtils.getNowDate());
        return userFavoriteMapper.insertUserFavorite(userFavorite);
    }

    /**
     * 修改用户机构收藏
     *
     * @param userFavorite 用户机构收藏
     * @return 结果
     */
    @Override
    public int updateUserFavorite(UserFavorite userFavorite)
    {
        userFavorite.setUpdateTime(DateUtils.getNowDate());
        return userFavoriteMapper.updateUserFavorite(userFavorite);
    }

    /**
     * 批量删除用户机构收藏
     *
     * @param favoriteIds 需要删除的用户机构收藏主键
     * @return 结果
     */
    @Override
    public int deleteUserFavoriteByFavoriteIds(Long[] favoriteIds)
    {
        return userFavoriteMapper.deleteUserFavoriteByFavoriteIds(favoriteIds);
    }

    /**
     * 删除用户机构收藏信息
     *
     * @param favoriteId 用户机构收藏主键
     * @return 结果
     */
    @Override
    public int deleteUserFavoriteByFavoriteId(Long favoriteId)
    {
        return userFavoriteMapper.deleteUserFavoriteByFavoriteId(favoriteId);
    }

    /**
     * 检查用户是否已收藏机构
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return true-已收藏 false-未收藏
     */
    @Override
    public boolean checkUserFavorite(Long userId, Long institutionId)
    {
        UserFavorite favorite = userFavoriteMapper.selectByUserIdAndInstitutionId(userId, institutionId);
        return favorite != null;
    }

    /**
     * 用户收藏机构
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return 结果
     */
    @Override
    public int userFavoriteInstitution(Long userId, Long institutionId)
    {
        // 检查是否已收藏
        if (checkUserFavorite(userId, institutionId)) {
            return 0; // 已收藏，无需重复收藏
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setInstitutionId(institutionId);
        return insertUserFavorite(favorite);
    }

    /**
     * 用户取消收藏机构
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return 结果
     */
    @Override
    public int userUnfavoriteInstitution(Long userId, Long institutionId)
    {
        return userFavoriteMapper.deleteByUserIdAndInstitutionId(userId, institutionId);
    }

    /**
     * 获取用户的收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    @Override
    public int getUserFavoriteCount(Long userId)
    {
        UserFavorite query = new UserFavorite();
        query.setUserId(userId);
        List<UserFavorite> list = selectUserFavoriteList(query);
        return list != null ? list.size() : 0;
    }
}