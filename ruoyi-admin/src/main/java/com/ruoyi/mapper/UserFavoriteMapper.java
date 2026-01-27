package com.ruoyi.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.domain.UserFavorite;

/**
 * 用户机构收藏Mapper接口
 *
 * @author ruoyi
 * @date 2024-12-09
 */
public interface UserFavoriteMapper
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
     * 修改用户机��收藏
     *
     * @param userFavorite 用户机构收藏
     * @return 结果
     */
    public int updateUserFavorite(UserFavorite userFavorite);

    /**
     * 删除用户机构收藏
     *
     * @param favoriteId 用户机构收藏主键
     * @return 结果
     */
    public int deleteUserFavoriteByFavoriteId(Long favoriteId);

    /**
     * 批量删除用户机构收藏
     *
     * @param favoriteIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserFavoriteByFavoriteIds(Long[] favoriteIds);

    /**
     * 根据用户ID和机构ID查询收藏记录
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return 用户机构收藏
     */
    public UserFavorite selectByUserIdAndInstitutionId(@Param("userId") Long userId, @Param("institutionId") Long institutionId);

    /**
     * 根据用户ID和机构ID删除收藏记录
     *
     * @param userId 用户ID
     * @param institutionId 机构ID
     * @return 结果
     */
    public int deleteByUserIdAndInstitutionId(@Param("userId") Long userId, @Param("institutionId") Long institutionId);
}