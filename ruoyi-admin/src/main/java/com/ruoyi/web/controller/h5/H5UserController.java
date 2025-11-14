package com.ruoyi.web.controller.h5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.MessageUtils;
import org.springframework.util.DigestUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.IElderInfoService;

/**
 * H5用户Controller
 * 用于H5移动端的用户认证和管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/user")
public class H5UserController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IElderFamilyService elderFamilyService;

    @Autowired
    private IElderInfoService elderInfoService;

    /**
     * H5登录 - 手机号+密码
     *
     * @param loginMap 登录信息 {phone, password}
     * @return token和用户信息
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> loginMap)
    {
        String phone = loginMap.get("phone");
        String password = loginMap.get("password");

        // 参数校验
        if (StringUtils.isEmpty(phone))
        {
            return AjaxResult.error("手机号不能为空");
        }
        if (StringUtils.isEmpty(password))
        {
            return AjaxResult.error("密码不能为空");
        }

        // 手机号格式校验
        if (!phone.matches("^1[3-9]\\d{9}$"))
        {
            return AjaxResult.error("手机号格式不正确");
        }

        // 根据手机号查询用户(直接使用Mapper,避免数据权限切面拦截)
        SysUser user = userMapper.selectUserByPhonenumber(phone);

        if (user == null)
        {
            return AjaxResult.error("该手机号未注册");
        }

        // 验证用户状态
        if ("1".equals(user.getStatus()))
        {
            return AjaxResult.error("账号已停用,请联系管理员");
        }
        if ("2".equals(user.getDelFlag()))
        {
            return AjaxResult.error("账号已删除");
        }

        // 验证密码 - 使用MD5
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword()))
        {
            return AjaxResult.error("密码错误");
        }

        // 创建登录用户对象
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUser(user);

        // 生成token
        String token = tokenService.createToken(loginUser);

        // 查询用户关联的老人列表
        List<ElderInfo> elders = getEldersByUserId(user.getUserId());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", buildUserInfo(user));
        data.put("elders", elders);

        return AjaxResult.success(data);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public AjaxResult getInfo()
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();

            // 查询用户关联的老人列表
            List<ElderInfo> elders = getEldersByUserId(user.getUserId());

            Map<String, Object> data = new HashMap<>();
            data.put("user", buildUserInfo(user));
            data.put("elders", elders);

            return AjaxResult.success(data);
        }
        catch (Exception e)
        {
            return AjaxResult.error("获取用户信息失败:" + e.getMessage());
        }
    }

    /**
     * 退出登录
     *
     * @return 结果
     */
    @PostMapping("/logout")
    @Log(title = "H5用户退出", businessType = BusinessType.OTHER)
    public AjaxResult logout()
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null)
            {
                // 删除用户缓存记录
                tokenService.delLoginUser(loginUser.getToken());
            }
            return AjaxResult.success("退出成功");
        }
        catch (Exception e)
        {
            return AjaxResult.error("退出失败:" + e.getMessage());
        }
    }

    /**
     * 获取用户关联的老人列表
     *
     * @param userId 用户ID
     * @return 老人列表
     */
    private List<ElderInfo> getEldersByUserId(Long userId)
    {
        // 查询用户关联的家属关系
        ElderFamily queryFamily = new ElderFamily();
        queryFamily.setUserId(userId);
        List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(queryFamily);

        // 获取所有关联的老人信息
        List<ElderInfo> elders = new ArrayList<>();
        for (ElderFamily family : familyList)
        {
            ElderInfo elder = elderInfoService.selectElderInfoByElderId(family.getElderId());
            if (elder != null)
            {
                elders.add(elder);
            }
        }
        return elders;
    }

    /**
     * 构建用户信息(脱敏处理)
     */
    private Map<String, Object> buildUserInfo(SysUser user)
    {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("userName", user.getUserName());
        userInfo.put("nickName", user.getNickName());
        userInfo.put("phonenumber", user.getPhonenumber());
        userInfo.put("sex", user.getSex());
        userInfo.put("avatar", user.getAvatar());
        // 不返回密码等敏感信息
        return userInfo;
    }
}
