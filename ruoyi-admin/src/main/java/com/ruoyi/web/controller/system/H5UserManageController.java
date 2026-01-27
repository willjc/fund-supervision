package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;

/**
 * H5用户管理Controller
 * 用于管理后台的H5家属用户管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/h5userManage")
public class H5UserManageController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询H5用户列表（家属用户）
     */
    @PreAuthorize("@ss.hasPermi('h5user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userMapper.selectH5UserList(user);
        return getDataTable(list);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('h5user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        SysUser sysUser = userMapper.selectH5UserWithElders(userId);
        if (sysUser == null)
        {
            return error("用户不存在");
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", sysUser);
        return ajax;
    }

    /**
     * 新增H5用户（家属用户）
     */
    @PreAuthorize("@ss.hasPermi('h5user:add')")
    @Log(title = "H5用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysUser user)
    {
        // 校验手机号格式
        if (StringUtils.isEmpty(user.getPhonenumber()))
        {
            return error("手机号不能为空");
        }
        if (!user.getPhonenumber().matches("^1[3-9]\\d{9}$"))
        {
            return error("手机号格式不正确");
        }

        // 检查手机号是否已存在
        SysUser existUser = userMapper.selectUserByPhonenumber(user.getPhonenumber());
        if (existUser != null)
        {
            return error("该手机号已注册");
        }

        // 使用手机号作为用户名
        user.setUserName(user.getPhonenumber());

        // 自动生成6位数字密码
        String password = generateRandomPassword(6);
        user.setPassword(SecurityUtils.encryptPassword(password));

        // 设置默认值
        user.setStatus("0"); // 正常状态
        user.setSex("0");   // 默认性别
        user.setCreateBy(getUsername());

        int result = userService.insertUser(user);
        if (result > 0)
        {
            // 返回用户信息和生成的密码
            AjaxResult ajax = AjaxResult.success("新增成功");
            ajax.put("userId", user.getUserId());
            ajax.put("password", password); // 返回明文密码供管理员查看
            return ajax;
        }
        return error("新增失败");
    }

    /**
     * 修改用户信息
     */
    @PreAuthorize("@ss.hasPermi('h5user:edit')")
    @Log(title = "H5用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('h5user:remove')")
    @Log(title = "H5用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (containsCurrentUserId(userIds))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('h5user:resetPwd')")
    @Log(title = "H5用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody Map<String, Object> params)
    {
        Long userId = Long.valueOf(params.get("userId").toString());
        SysUser user = userService.selectUserById(userId);
        if (user == null)
        {
            return error("用户不存在");
        }

        // 自动生成新的6位数字密码
        String password = generateRandomPassword(6);
        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setUpdateBy(getUsername());
        int result = userService.resetPwd(user);

        if (result > 0)
        {
            AjaxResult ajax = AjaxResult.success("重置成功");
            ajax.put("password", password); // 返回新密码
            return ajax;
        }
        return error("重置失败");
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword(int length)
    {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 检查用户ID数组中是否包含当前用户ID
     */
    private boolean containsCurrentUserId(Long[] userIds)
    {
        Long currentUserId = getUserId();
        if (userIds != null && userIds.length > 0)
        {
            for (Long userId : userIds)
            {
                if (userId != null && userId.equals(currentUserId))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
