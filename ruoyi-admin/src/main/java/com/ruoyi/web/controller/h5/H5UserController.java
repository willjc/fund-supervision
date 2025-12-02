package com.ruoyi.web.controller.h5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.util.DigestUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.domain.ElderAttachment;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.service.IElderAttachmentService;

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

    @Autowired
    private IElderAttachmentService elderAttachmentService;

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
        List<Map<String, Object>> elders = getEldersByUserId(user.getUserId());

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
            List<Map<String, Object>> elders = getEldersByUserId(user.getUserId());

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
     * 获取用户关联的老人列表(包含家属关系)
     *
     * @param userId 用户ID
     * @return 老人列表(包含关系信息)
     */
    private List<Map<String, Object>> getEldersByUserId(Long userId)
    {
        // 查询用户关联的家属关系
        ElderFamily queryFamily = new ElderFamily();
        queryFamily.setUserId(userId);
        List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(queryFamily);

        // 获取所有关联的老人信息,并附加家属关系
        List<Map<String, Object>> elders = new ArrayList<>();
        for (ElderFamily family : familyList)
        {
            ElderInfo elder = elderInfoService.selectElderInfoByElderId(family.getElderId());
            if (elder != null)
            {
                Map<String, Object> elderData = new HashMap<>();
                elderData.put("elderId", elder.getElderId());
                elderData.put("elderName", elder.getElderName());
                elderData.put("gender", elder.getGender());
                elderData.put("idCard", elder.getIdCard());
                elderData.put("birthDate", elder.getBirthDate());
                elderData.put("age", elder.getAge());
                elderData.put("phone", elder.getPhone());
                elderData.put("address", elder.getAddress());
                elderData.put("healthStatus", elder.getHealthStatus());
                elderData.put("photoPath", elder.getPhotoPath());
                // 附加家属关系信息
                elderData.put("relationType", family.getRelationType());
                elderData.put("relationName", family.getRelationName());
                elderData.put("isMainContact", family.getIsMainContact());
                elders.add(elderData);
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

    /**
     * 新增老人信息
     *
     * @param params 包含��人信息和图片路径
     * @return 操作结果
     */
    @PostMapping("/addElder")
    @Log(title = "新增老人信息", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addElder(@RequestBody Map<String, Object> params)
    {
        try
        {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error("用户未登录");
            }
            Long userId = loginUser.getUser().getUserId();

            // 解析参数
            String elderName = (String) params.get("elderName");
            String relationType = (String) params.get("relationType");
            String ageStr = (String) params.get("age");
            String idCard = (String) params.get("idCard");
            String phone = (String) params.get("phone");
            String address = (String) params.get("address");
            String healthStatus = (String) params.get("healthStatus");
            // String medicalHistory = (String) params.get("medicalHistory"); // 暂时注释，ElderInfo实体中无此字段
            String photoPath = (String) params.get("photoPath");

            // 身份证正面和反面路径
            String idCardFrontPath = (String) params.get("idCardFrontPath");
            String idCardBackPath = (String) params.get("idCardBackPath");

            // 参数校验
            if (StringUtils.isEmpty(elderName)) {
                return AjaxResult.error("老人姓名不能为空");
            }
            if (StringUtils.isEmpty(relationType)) {
                return AjaxResult.error("与本人关系不能为空");
            }
            if (StringUtils.isEmpty(ageStr)) {
                return AjaxResult.error("年龄不能为空");
            }
            if (StringUtils.isEmpty(idCard)) {
                return AjaxResult.error("身份证号不能为空");
            }

            // 转换年龄
            Integer age;
            try {
                age = Integer.parseInt(ageStr);
                if (age <= 0 || age > 150) {
                    return AjaxResult.error("年龄必须在1-150之间");
                }
            } catch (NumberFormatException e) {
                return AjaxResult.error("年龄格式不正确");
            }

            // 身份证号格式校验
            if (!idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")) {
                return AjaxResult.error("身份证号格式不正确");
            }

            // 手机号格式校验（可选）
            if (StringUtils.isNotEmpty(phone) && !phone.matches("^1[3-9]\\d{9}$")) {
                return AjaxResult.error("手机号格式不正确");
            }

            // 根据身份证号解析出生日期和性别
            Date birthDate = null;
            String gender = "1"; // 默认男性

            if (StringUtils.isNotEmpty(idCard) && idCard.length() == 18) {
                try {
                    // 解析出生日期：身份证第7-14位为出生日期 YYYYMMDD
                    String birthDateStr = idCard.substring(6, 14);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    birthDate = sdf.parse(birthDateStr);

                    // 解析性别：身份证第17位，奇数为男性，偶数为女性
                    String genderCode = idCard.substring(16, 17);
                    int genderNum = Integer.parseInt(genderCode);
                    gender = (genderNum % 2 == 1) ? "1" : "2"; // 1-男性 2-女性
                } catch (Exception e) {
                    System.err.println("身份证号解析失败: " + e.getMessage());
                    // 解析失败时使用默认值
                    birthDate = new Date(); // 使用当前日期作为默认
                    gender = "1";
                }
            } else {
                // 身份证号格式不正确时使用当前日期
                birthDate = new Date();
            }

            // 创建老人信息
            ElderInfo elderInfo = new ElderInfo();
            elderInfo.setElderName(elderName);
            elderInfo.setGender(gender); // 根据身份证解析的性别
            elderInfo.setIdCard(idCard);
            elderInfo.setBirthDate(birthDate); // 设置出生日期
            elderInfo.setAge(age != null ? age.longValue() : null); // 转换Integer到Long
            elderInfo.setPhone(StringUtils.isEmpty(phone) ? null : phone);
            elderInfo.setAddress(StringUtils.isEmpty(address) ? null : address);
            elderInfo.setHealthStatus(StringUtils.isEmpty(healthStatus) ? null : healthStatus);
            // medicalHistory字段不存在，暂时去掉
            elderInfo.setPhotoPath(StringUtils.isEmpty(photoPath) ? null : photoPath);
            // sourceType和submitUserId字段不存在，暂时去掉
            elderInfo.setStatus("1"); // 状态：1-正常
            elderInfo.setCareLevel("1"); // 护理等级：1-自理

            // 检查身份证号是否已存在
            ElderInfo existingElder = elderInfoService.selectElderInfoByIdCard(idCard);
            if (existingElder != null) {
                return AjaxResult.error("该身份证号已被使用，请检查身份证号是否正确");
            }

            // 保存老人信息
            int elderResult = elderInfoService.insertElderInfo(elderInfo);
            if (elderResult <= 0) {
                return AjaxResult.error("保存老人信息失败");
            }

            Long elderId = elderInfo.getElderId();

            // 保存身份证正面附件
            if (StringUtils.isNotEmpty(idCardFrontPath)) {
                ElderAttachment frontAttachment = new ElderAttachment();
                frontAttachment.setElderId(elderId);
                frontAttachment.setAttachmentType("1"); // 1-身份证正面
                frontAttachment.setFilePath(idCardFrontPath);
                frontAttachment.setFileName("身份证正面_" + elderId + ".jpg");
                elderAttachmentService.insertElderAttachment(frontAttachment);
            }

            // 保存身份证反面附件
            if (StringUtils.isNotEmpty(idCardBackPath)) {
                ElderAttachment backAttachment = new ElderAttachment();
                backAttachment.setElderId(elderId);
                backAttachment.setAttachmentType("2"); // 2-身份证反面
                backAttachment.setFilePath(idCardBackPath);
                backAttachment.setFileName("身份证反面_" + elderId + ".jpg");
                elderAttachmentService.insertElderAttachment(backAttachment);
            }

            // 创建家属关系
            ElderFamily family = new ElderFamily();
            family.setUserId(userId);
            family.setElderId(elderId);
            family.setRelationType(relationType);
            family.setRelationName(getRelationNameByType(relationType));
            family.setIsDefault("0"); // 不是默认老人
            family.setIsMainContact("0"); // 不是主要联系人
            family.setStatus("0"); // 状态正常
            elderFamilyService.insertElderFamily(family);

            return AjaxResult.success("新增老人信息成功");

        } catch (Exception e) {
            // 记录错误日志
            e.printStackTrace();
            return AjaxResult.error("新增老人信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取老人详情信息
     *
     * @param elderId 老人ID
     * @return 老人详情
     */
    @GetMapping("/getElderById")
    public AjaxResult getElderById(@RequestParam("elderId") Long elderId)
    {
        try
        {
            // 查询老人基本信息
            ElderInfo elderInfo = elderInfoService.selectElderInfoByElderId(elderId);
            if (elderInfo == null) {
                return AjaxResult.error("老人信息不存在");
            }

            // 查询老人附件信息
            List<ElderAttachment> attachments = elderAttachmentService.selectAttachmentsByElderId(elderId);

            // 查询家属关系信息
            ElderFamily familyRelation = null;
            ElderFamily queryParam = new ElderFamily();
            queryParam.setElderId(elderId);
            queryParam.setUserId(SecurityUtils.getUserId());
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(queryParam);
            if (familyList != null && familyList.size() > 0) {
                familyRelation = familyList.get(0);
            }

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("elderInfo", elderInfo);
            data.put("attachments", attachments);
            data.put("familyRelation", familyRelation);

            return AjaxResult.success(data);
        }
        catch (Exception e)
        {
            return AjaxResult.error("获取老人信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新老人信息
     *
     * @param params 更新参数
     * @return 操作结果
     */
    @PostMapping("/updateElder")
    @Log(title = "更新老人信息", businessType = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateElder(@RequestBody Map<String, Object> params)
    {
        try
        {
            String elderIdStr = (String) params.get("elderId");
            String elderName = (String) params.get("elderName");
            String ageStr = (String) params.get("age");
            String idCard = (String) params.get("idCard");
            String phone = (String) params.get("phone");
            String address = (String) params.get("address");
            String healthStatus = (String) params.get("healthStatus");
            String photoPath = (String) params.get("photoPath");
            String idCardFrontPath = (String) params.get("idCardFrontPath");
            String idCardBackPath = (String) params.get("idCardBackPath");

            // 参数校验
            if (StringUtils.isEmpty(elderIdStr)) {
                return AjaxResult.error("老人ID不能为空");
            }
            if (StringUtils.isEmpty(elderName)) {
                return AjaxResult.error("老人姓名不能为空");
            }

            Long elderId = Long.valueOf(elderIdStr);
            Integer age = StringUtils.isNotEmpty(ageStr) ? Integer.valueOf(ageStr) : null;

            // 检查老人是否存在
            ElderInfo existingElder = elderInfoService.selectElderInfoByElderId(elderId);
            if (existingElder == null) {
                return AjaxResult.error("老人信息不存在");
            }

            // 更新老人基本信息
            ElderInfo elderInfo = new ElderInfo();
            elderInfo.setElderId(elderId);
            elderInfo.setElderName(elderName);
            elderInfo.setAge(age != null ? age.longValue() : null);
            elderInfo.setPhone(StringUtils.isEmpty(phone) ? null : phone);
            elderInfo.setAddress(StringUtils.isEmpty(address) ? null : address);
            elderInfo.setHealthStatus(StringUtils.isEmpty(healthStatus) ? null : healthStatus);
            elderInfo.setPhotoPath(StringUtils.isEmpty(photoPath) ? null : photoPath);

            // 如果身份证号发生变化，检查是否重复
            if (StringUtils.isNotEmpty(idCard) && !idCard.equals(existingElder.getIdCard())) {
                ElderInfo duplicateElder = elderInfoService.selectElderInfoByIdCard(idCard);
                if (duplicateElder != null && !duplicateElder.getElderId().equals(elderId)) {
                    return AjaxResult.error("该身份证号已被使用");
                }
                elderInfo.setIdCard(idCard);
            }

            int result = elderInfoService.updateElderInfo(elderInfo);
            if (result <= 0) {
                return AjaxResult.error("更新老人信息失败");
            }

            // 处理附件信息
            if (StringUtils.isNotEmpty(idCardFrontPath)) {
                ElderAttachment frontAttachment = elderAttachmentService.selectAttachmentByElderIdAndType(elderId, "1");
                if (frontAttachment != null) {
                    frontAttachment.setFilePath(idCardFrontPath);
                    elderAttachmentService.updateElderAttachment(frontAttachment);
                } else {
                    frontAttachment = new ElderAttachment();
                    frontAttachment.setElderId(elderId);
                    frontAttachment.setAttachmentType("1");
                    frontAttachment.setFilePath(idCardFrontPath);
                    elderAttachmentService.insertElderAttachment(frontAttachment);
                }
            }

            if (StringUtils.isNotEmpty(idCardBackPath)) {
                ElderAttachment backAttachment = elderAttachmentService.selectAttachmentByElderIdAndType(elderId, "2");
                if (backAttachment != null) {
                    backAttachment.setFilePath(idCardBackPath);
                    elderAttachmentService.updateElderAttachment(backAttachment);
                } else {
                    backAttachment = new ElderAttachment();
                    backAttachment.setElderId(elderId);
                    backAttachment.setAttachmentType("2");
                    backAttachment.setFilePath(idCardBackPath);
                    elderAttachmentService.insertElderAttachment(backAttachment);
                }
            }

            return AjaxResult.success("更新老人信息成功");
        }
        catch (Exception e)
        {
            return AjaxResult.error("更新老人信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据关系类型获取关系名称
     */
    private String getRelationNameByType(String relationType) {
        switch (relationType) {
            case "1": return "子女";
            case "2": return "配偶";
            case "3": return "兄弟姐妹";
            case "4": return "其他亲属";
            case "5": return "朋友";
            default: return "家属";
        }
    }
}
