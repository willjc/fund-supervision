package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.ruoyi.mapper.ElderFamilyMapper;
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.service.IElderFamilyService;

/**
 * 用户-老人关联(家属管理)Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-14
 */
@Service
public class ElderFamilyServiceImpl implements IElderFamilyService
{
    @Autowired
    private ElderFamilyMapper elderFamilyMapper;

    @Autowired
    private ISysUserService userService;

    /**
     * 查询家属关联
     *
     * @param familyId 家属关联主键
     * @return 家属关联
     */
    @Override
    public ElderFamily selectElderFamilyById(Long familyId)
    {
        return elderFamilyMapper.selectElderFamilyById(familyId);
    }

    /**
     * 查询家属关联列表
     *
     * @param elderFamily 家属关联
     * @return 家属关联
     */
    @Override
    public List<ElderFamily> selectElderFamilyList(ElderFamily elderFamily)
    {
        return elderFamilyMapper.selectElderFamilyList(elderFamily);
    }

    /**
     * 新增家属关联
     *
     * @param elderFamily 家属关联
     * @return 结果
     */
    @Override
    public int insertElderFamily(ElderFamily elderFamily)
    {
        // 根据手机号查找用户ID
        String phonenumber = elderFamily.getPhonenumber();
        if (phonenumber != null && !phonenumber.isEmpty()) {
            SysUser queryUser = new SysUser();
            queryUser.setPhonenumber(phonenumber);
            List<SysUser> userList = userService.selectUserList(queryUser);

            if (userList == null || userList.isEmpty()) {
                // 用户不存在,自动创建家属账号
                SysUser newUser = new SysUser();
                newUser.setPhonenumber(phonenumber);
                newUser.setUserName(phonenumber); // 用户名设为手机号
                newUser.setNickName(elderFamily.getRelationName() != null ? elderFamily.getRelationName() : "家属");
                // 使用MD5加密密码
                String md5Password = DigestUtils.md5DigestAsHex("123456".getBytes());
                newUser.setPassword(md5Password); // 默认密码123456
                newUser.setStatus("0"); // 正常状态
                newUser.setCreateTime(DateUtils.getNowDate());

                userService.insertUser(newUser);
                elderFamily.setUserId(newUser.getUserId());
            } else {
                elderFamily.setUserId(userList.get(0).getUserId());
            }
        }

        elderFamily.setCreateTime(DateUtils.getNowDate());
        elderFamily.setStatus("0"); // 默认状态为正常
        return elderFamilyMapper.insertElderFamily(elderFamily);
    }

    /**
     * 修改家属关联
     *
     * @param elderFamily 家属关联
     * @return 结果
     */
    @Override
    public int updateElderFamily(ElderFamily elderFamily)
    {
        elderFamily.setUpdateTime(DateUtils.getNowDate());
        return elderFamilyMapper.updateElderFamily(elderFamily);
    }

    /**
     * 批量删除家属关联
     *
     * @param familyIds 需要删除的家属关联主键
     * @return 结果
     */
    @Override
    public int deleteElderFamilyByIds(Long[] familyIds)
    {
        return elderFamilyMapper.deleteElderFamilyByIds(familyIds);
    }

    /**
     * 删除家属关联信息
     *
     * @param familyId 家属关联主键
     * @return 结果
     */
    @Override
    public int deleteElderFamilyById(Long familyId)
    {
        return elderFamilyMapper.deleteElderFamilyById(familyId);
    }
}
