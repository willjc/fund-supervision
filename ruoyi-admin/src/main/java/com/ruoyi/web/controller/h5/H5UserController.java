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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.web.core.domain.ZhbUserInfo;

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
    private static final Logger logger = LoggerFactory.getLogger(H5UserController.class);
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
        String account = loginMap.get("account");      // 修改为account(支持手机号或身份证号)
        String password = loginMap.get("password");

        // 参数校验
        if (StringUtils.isEmpty(account))
        {
            return AjaxResult.error("账号不能为空");
        }
        if (StringUtils.isEmpty(password))
        {
            return AjaxResult.error("密码不能为空");
        }

        SysUser user = null;
        ElderInfo elderInfo = null;
        boolean isElderLogin = false;  // 标记是否为老人身份证号登录

        // 判断是手机号还是身份证号
        if (account.matches("^1[3-9]\\d{9}$")) {
            // 手机号登录 - 查询sys_user表
            user = userMapper.selectUserByPhonenumber(account);
            if (user == null) {
                return AjaxResult.error("该手机号未注册");
            }
        } else if (account.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")) {
            // 身份证号登录 - 查询elder_info表
            elderInfo = elderInfoService.selectElderInfoByIdCard(account);
            if (elderInfo == null) {
                return AjaxResult.error("该身份证号未注册");
            }
            if (StringUtils.isEmpty(elderInfo.getPassword())) {
                return AjaxResult.error("该老人未设置密码,请联系管理员");
            }
            isElderLogin = true;

            // 根据老人信息查找或创建对应的sys_user账号
            user = getOrCreateUserForElder(elderInfo);
        } else {
            return AjaxResult.error("账号格式不正确,请输入手机号或身份证号");
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

        // 密码验证 - 统一使用MD5
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 如果是老人登录,验证elder_info表的密码
        // 如果是家属登录,验证sys_user表的密码
        String storedPassword = isElderLogin ? elderInfo.getPassword() : user.getPassword();

        if (!md5Password.equals(storedPassword))
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

        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", buildUserInfo(user));
        data.put("elders", elders);
        data.put("loginType", isElderLogin ? "idcard" : "phone");  // 标记登录方式

        return AjaxResult.success(data);
    }

    /**
     * 为老人获取或创建对应的sys_user账号
     * 身份证登录创建独立账号，不与家属账号混淆
     *
     * @param elderInfo 老人信息
     * @return sys_user账号
     */
    private SysUser getOrCreateUserForElder(ElderInfo elderInfo)
    {
        // 身份证登录：使用身份证号作为用户名创建独立的老人账号
        String userName = "elder_" + elderInfo.getIdCard();  // 添加前缀避免与家属用户名冲突

        // 先尝试查找是否已存在该老人的独立账号
        SysUser existingUser = userService.selectUserByUserName(userName);
        if (existingUser != null) {
            return existingUser;
        }

        // 创建新的老人独立账号
        SysUser newUser = new SysUser();
        newUser.setUserName(userName);  // 使用 elder_身份证号 作为用户名
        newUser.setNickName(elderInfo.getElderName());
        newUser.setPhonenumber(elderInfo.getPhone());  // 可能为空
        newUser.setPassword(elderInfo.getPassword());  // 使用老人的密码
        newUser.setStatus("0");
        newUser.setCreateTime(new Date());
        newUser.setCreateBy("system");  // 标记为系统创建

        // 插入用户
        userService.insertUser(newUser);

        // 创建elder_family关联关系，让老人账号关联自己（关系类型设为"本人"）
        ElderFamily selfRelation = new ElderFamily();
        selfRelation.setUserId(newUser.getUserId());
        selfRelation.setElderId(elderInfo.getElderId());
        selfRelation.setRelationType("0");  // 0表示本人
        selfRelation.setRelationName("本人");
        selfRelation.setIsDefault("1");   // 默认显示自己
        selfRelation.setIsMainContact("0");  // 不是主要联系人
        selfRelation.setStatus("0");  // 状态正常
        selfRelation.setCreateBy("system");
        selfRelation.setCreateTime(new Date());

        elderFamilyService.insertElderFamily(selfRelation);

        return newUser;
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
        userInfo.put("realName", user.getRealName());
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
            String emergencyContact = (String) params.get("emergencyContact");
            String emergencyPhone = (String) params.get("emergencyPhone");
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
            // 紧急联系人必填校验
            if (StringUtils.isEmpty(emergencyContact)) {
                return AjaxResult.error("紧急联系人不能为空");
            }
            if (StringUtils.isEmpty(emergencyPhone)) {
                return AjaxResult.error("紧急联系电话不能为空");
            }
            if (!emergencyPhone.matches("^1[3-9]\\d{9}$")) {
                return AjaxResult.error("紧急联系电话格式不正确");
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
            elderInfo.setEmergencyContact(emergencyContact); // 紧急联系人
            elderInfo.setEmergencyPhone(emergencyPhone); // 紧急联系电话
            // medicalHistory字段不存在，暂时去掉
            elderInfo.setPhotoPath(StringUtils.isEmpty(photoPath) ? null : photoPath);
            // 设置身份证照片路径到 elder_info 表（机构端需要查看）
            elderInfo.setIdCardFrontPath(StringUtils.isEmpty(idCardFrontPath) ? null : idCardFrontPath);
            elderInfo.setIdCardBackPath(StringUtils.isEmpty(idCardBackPath) ? null : idCardBackPath);
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
            String emergencyContact = (String) params.get("emergencyContact");
            String emergencyPhone = (String) params.get("emergencyPhone");
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
            // 紧急联系人必填校验
            if (StringUtils.isEmpty(emergencyContact)) {
                return AjaxResult.error("紧急联系人不能为空");
            }
            if (StringUtils.isEmpty(emergencyPhone)) {
                return AjaxResult.error("紧急联系电话不能为空");
            }
            if (!emergencyPhone.matches("^1[3-9]\\d{9}$")) {
                return AjaxResult.error("紧急联系电话格式不正确");
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
            elderInfo.setEmergencyContact(emergencyContact);
            elderInfo.setEmergencyPhone(emergencyPhone);
            elderInfo.setPhotoPath(StringUtils.isEmpty(photoPath) ? null : photoPath);
            // 设置身份证照片路径到 elder_info 表（机构端需要查看）
            elderInfo.setIdCardFrontPath(StringUtils.isEmpty(idCardFrontPath) ? null : idCardFrontPath);
            elderInfo.setIdCardBackPath(StringUtils.isEmpty(idCardBackPath) ? null : idCardBackPath);

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

    /**
     * 更新个人资料
     *
     * @param params 包含 nickName, realName, sex
     * @return 操作结果
     */
    @PutMapping("/profile")
    @Log(title = "H5用户更新资料", businessType = BusinessType.UPDATE)
    public AjaxResult updateProfile(@RequestBody Map<String, String> params)
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error("用户未登录");
            }

            SysUser currentUser = loginUser.getUser();
            String nickName = params.get("nickName");
            String realName = params.get("realName");
            String sex = params.get("sex");

            // 只更新传入的字段
            if (StringUtils.isNotEmpty(nickName)) {
                currentUser.setNickName(nickName);
            }
            if (StringUtils.isNotEmpty(realName)) {
                currentUser.setRealName(realName);
            }
            if (StringUtils.isNotEmpty(sex)) {
                currentUser.setSex(sex);
            }

            // 更新数据库
            int result = userService.updateUserProfile(currentUser);
            if (result > 0) {
                // 更新缓存
                tokenService.setLoginUser(loginUser);
                return AjaxResult.success("更新成功");
            }
            return AjaxResult.error("更新失败");
        }
        catch (Exception e)
        {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @PostMapping("/avatar")
    @Log(title = "H5用户上传头像", businessType = BusinessType.UPDATE)
    public AjaxResult uploadAvatar(@RequestParam("file") MultipartFile file) throws Exception
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error("用户未登录");
            }

            if (file.isEmpty()) {
                return AjaxResult.error("请选择要上传的文件");
            }

            // 上传头像文件
            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);

            // 更新数据库
            if (userService.updateUserAvatar(loginUser.getUserId(), avatar)) {
                // 删除旧头像
                String oldAvatar = loginUser.getUser().getAvatar();
                if (StringUtils.isNotEmpty(oldAvatar)) {
                    FileUtils.deleteFile(RuoYiConfig.getProfile() + FileUtils.stripPrefix(oldAvatar));
                }

                // 更新缓存
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);

                AjaxResult ajax = AjaxResult.success();
                ajax.put("avatar", avatar);
                return ajax;
            }
            return AjaxResult.error("上传失败");
        }
        catch (Exception e)
        {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 郑好办登录
     *
     * @param loginMap 登录信息 {authCode}
     * @return token和用户信息
     */
    @PostMapping("/login/zhb")
    @Log(title = "H5郑好办登录", businessType = BusinessType.OTHER)
    public AjaxResult loginByZhengHaoBan(@RequestBody Map<String, String> loginMap)
    {
        String authCode = loginMap.get("authCode");

        // 参数校验
        if (StringUtils.isEmpty(authCode))
        {
            return AjaxResult.error("授权码不能为空");
        }

        try
        {
            // 1. 通过authCode获取郑好办用户信息
            ZhbUserInfo zhbUserInfo = zhbService.loginByAuthCode(authCode);
            if (zhbUserInfo == null)
            {
                return AjaxResult.error("获取郑好办用户信息失败，请重新授权");
            }

            // 2. 优先级查询用户：zid → 手机号 → 身份证号
            SysUser user = findUserByPriority(zhbUserInfo);
            boolean isNewUser = (user == null);

            if (isNewUser)
            {
                // 创建新用户
                user = createZhbUser(zhbUserInfo);
            }
            else
            {
                // 更新用户信息（绑定zid和更新其他信息）
                updateZhbUser(user, zhbUserInfo);
            }

            // 3. 验证用户状态
            if (user == null || "1".equals(user.getStatus()))
            {
                return AjaxResult.error("账号已停用");
            }
            if ("2".equals(user.getDelFlag()))
            {
                return AjaxResult.error("账号已删除");
            }

            // 4. 生成token
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(user.getUserId());
            loginUser.setUser(user);
            String token = tokenService.createToken(loginUser);

            // 5. 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", buildUserInfo(user));
            data.put("isNewUser", isNewUser);

            // 查询关联老人列表
            List<Map<String, Object>> elders = getEldersByUserId(user.getUserId());
            data.put("elders", elders);

            return AjaxResult.success(data);
        }
        catch (Exception e)
        {
            logger.error("郑好办登录失败", e);
            return AjaxResult.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 优先级查询用户
     * 1. 按zid查询（郑好办用户ID）
     * 2. 按手机号查询
     * 3. 按身份证号查询
     *
     * @param zhbUserInfo 郑好办用户信息
     * @return 用户对象，找不到返回null
     */
    private SysUser findUserByPriority(ZhbUserInfo zhbUserInfo)
    {
        // 1. 优先按zid查询
        String zid = zhbUserInfo.getZid();
        if (StringUtils.isNotEmpty(zid))
        {
            SysUser user = userMapper.selectUserByZid(zid);
            if (user != null)
            {
                logger.info("按zid找到用户，zid：{}，userId：{}", zid, user.getUserId());
                return user;
            }
        }

        // 2. 按手机号查询
        String phone = zhbUserInfo.getPhone();
        if (StringUtils.isNotEmpty(phone))
        {
            SysUser user = userMapper.selectUserByPhonenumber(phone);
            if (user != null)
            {
                logger.info("按手机号找到用户，phone：{}，userId：{}", phone, user.getUserId());
                return user;
            }
        }

        // 3. 按身份证号查询
        String idCard = zhbUserInfo.getIdCode();
        if (StringUtils.isNotEmpty(idCard))
        {
            SysUser user = userMapper.selectUserByIdCard(idCard);
            if (user != null)
            {
                logger.info("按身份证号找到用户，idCard：{}，userId：{}", idCard, user.getUserId());
                return user;
            }
        }

        logger.info("未找到匹配的用户，zid：{}，phone：{}，idCard：{}", zid, phone, idCard);
        return null;
    }

    /**
     * 创建郑好办用户
     */
    private SysUser createZhbUser(ZhbUserInfo zhbUserInfo)
    {
        SysUser newUser = new SysUser();
        newUser.setZid(zhbUserInfo.getZid());
        newUser.setUserName("zhb_" + zhbUserInfo.getZid());

        // 从郑好办获取的用户信息
        String nickName = zhbUserInfo.getDisplayName();
        String realName = zhbUserInfo.getRealName();
        String phone = zhbUserInfo.getPhone();
        String idCard = zhbUserInfo.getIdCode();
        Integer gender = zhbUserInfo.getGender(); // 0男 1女

        // 设置昵称，优先使用displayName，其次realName，最后使用默认值
        if (StringUtils.isNotEmpty(nickName))
        {
            newUser.setNickName(nickName);
        }
        else if (StringUtils.isNotEmpty(realName))
        {
            newUser.setNickName(realName);
        }
        else
        {
            newUser.setNickName("郑好办用户");
        }
        newUser.setRealName(realName);
        newUser.setPhonenumber(phone);
        newUser.setIdCard(idCard);

        // 郑好办性别：0男 1女，系统：0男 1女 2未知
        if (gender != null)
        {
            if (gender == 0)
            {
                newUser.setSex("0");
            }
            else if (gender == 1)
            {
                newUser.setSex("1");
            }
            else
            {
                newUser.setSex("2");
            }
        }
        else
        {
            newUser.setSex("2");
        }

        // 设置随机密码（郑好办用户不会使用密码登录）
        newUser.setPassword(SecurityUtils.encryptPassword(java.util.UUID.randomUUID().toString()));

        newUser.setStatus("0"); // 正常
        newUser.setDelFlag("0"); // 未删除
        newUser.setCreateTime(new Date());
        newUser.setCreateBy("zhb_system");

        // 插入用户
        userMapper.insertUser(newUser);

        logger.info("创建郑好办用户成功，userId：{}，zid：{}，phone：{}", newUser.getUserId(), zhbUserInfo.getZid(), phone);

        return newUser;
    }

    /**
     * 更新郑好办用户信息
     */
    private void updateZhbUser(SysUser user, ZhbUserInfo zhbUserInfo)
    {
        boolean needUpdate = false;

        // 绑定zid（如果还没有绑定）
        String zid = zhbUserInfo.getZid();
        if (StringUtils.isNotEmpty(zid) && !zid.equals(user.getZid()))
        {
            user.setZid(zid);
            needUpdate = true;
        }

        // 更新真实姓名
        String realName = zhbUserInfo.getRealName();
        if (StringUtils.isNotEmpty(realName) && !realName.equals(user.getRealName()))
        {
            user.setRealName(realName);
            needUpdate = true;
        }

        // 更新手机号
        String phone = zhbUserInfo.getPhone();
        if (StringUtils.isNotEmpty(phone) && !phone.equals(user.getPhonenumber()))
        {
            user.setPhonenumber(phone);
            needUpdate = true;
        }

        // 更新身份证号
        String idCard = zhbUserInfo.getIdCode();
        if (StringUtils.isNotEmpty(idCard) && !idCard.equals(user.getIdCard()))
        {
            user.setIdCard(idCard);
            needUpdate = true;
        }

        if (needUpdate)
        {
            user.setUpdateBy("zhb_system");
            user.setUpdateTime(new Date());
            userMapper.updateUser(user);
            logger.info("更新郑好办用户信息成功，userId：{}，zid已绑定：{}", user.getUserId(), zid);
        }
    }

    @Autowired
    private com.ruoyi.service.IZhengHaoBanService zhbService;
}
