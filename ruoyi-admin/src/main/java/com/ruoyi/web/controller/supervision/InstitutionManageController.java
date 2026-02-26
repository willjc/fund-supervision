package com.ruoyi.web.controller.supervision;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.InstitutionRating;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IInstitutionRatingService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 民政监管-机构管理Controller
 *
 * @author ruoyi
 * @date 2025-11-10
 */
@RestController
@RequestMapping("/supervision/institution")
public class InstitutionManageController extends BaseController
{
    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IInstitutionRatingService ratingService;

    @Autowired
    private com.ruoyi.service.IInstitutionBlacklistService institutionBlacklistService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 机构管理员角色ID
    private static final Long INSTITUTION_ADMIN_ROLE_ID = 100L;

    /**
     * 查询已创建机构账号列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:batchImport')")
    @GetMapping("/account/list")
    public TableDataInfo listInstitutionAccounts(PensionInstitution pensionInstitution)
    {
        startPage();
        // 处理前端传递的状态参数：字符串"null"转换为真正的null
        if ("null".equals(pensionInstitution.getStatus())) {
            pensionInstitution.setStatus(null);
        }
        // 批量导入页面显示所有机构，不限制状态（用户可通过筛选框选择特定状态）
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);

        // 补充用户名信息
        for (PensionInstitution institution : list) {
            try {
                String sql = "SELECT u.user_name, u.status as user_status FROM sys_user u " +
                             "INNER JOIN sys_user_institution ui ON u.user_id = ui.user_id " +
                             "WHERE ui.institution_id = ? LIMIT 1";
                Map<String, Object> userInfo = jdbcTemplate.queryForMap(sql, institution.getInstitutionId());
                institution.setRemark((String) userInfo.get("user_name")); // 临时使用remark字段存储用户名
            } catch (Exception e) {
                institution.setRemark("未关联账号");
            }
        }

        return getDataTable(list);
    }

    /**
     * 新增单个机构账号
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:add')")
    @Log(title = "新增机构账号", businessType = BusinessType.INSERT)
    @PostMapping("/account/add")
    public AjaxResult addInstitutionAccount(@RequestBody PensionInstitution pensionInstitution)
    {
        // 1. 验证必填项
        if (pensionInstitution.getInstitutionName() == null || pensionInstitution.getInstitutionName().trim().isEmpty()) {
            return AjaxResult.error("机构名称不能为空");
        }
        if (pensionInstitution.getCreditCode() == null || pensionInstitution.getCreditCode().trim().isEmpty()) {
            return AjaxResult.error("统一信用代码不能为空");
        }
        if (pensionInstitution.getContactPerson() == null || pensionInstitution.getContactPerson().trim().isEmpty()) {
            return AjaxResult.error("负责人姓名不能为空");
        }
        if (pensionInstitution.getContactPhone() == null || pensionInstitution.getContactPhone().trim().isEmpty()) {
            return AjaxResult.error("联系电话不能为空");
        }

        // 2. 验证信用代码唯一性
        PensionInstitution query = new PensionInstitution();
        query.setCreditCode(pensionInstitution.getCreditCode());
        List<PensionInstitution> existList = pensionInstitutionService.selectPensionInstitutionList(query);
        if (existList != null && !existList.isEmpty()) {
            return AjaxResult.error("统一信用代码已存在: " + pensionInstitution.getCreditCode());
        }

        // 3. 保存机构基本信息(status设为null,表示未申请入驻状态)
        pensionInstitution.setStatus(null);
        pensionInstitution.setCreateBy(getUsername());
        pensionInstitution.setCreateTime(new Date());
        int result = pensionInstitutionService.insertPensionInstitution(pensionInstitution);

        if (result > 0 && pensionInstitution.getInstitutionId() != null) {
            // 4. 生成机构管理员账号
            Map<String, String> accountInfo = createInstitutionUser(
                pensionInstitution.getInstitutionId(),
                pensionInstitution.getInstitutionName(),
                pensionInstitution.getContactPerson(),
                pensionInstitution.getContactPhone()
            );

            // 5. 返回生成的账号信息
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("institutionId", pensionInstitution.getInstitutionId());
            resultData.put("institutionName", pensionInstitution.getInstitutionName());
            resultData.put("userName", accountInfo.get("userName"));
            resultData.put("password", accountInfo.get("password"));
            resultData.put("message", "机构账号创建成功");

            return AjaxResult.success(resultData);
        }

        return AjaxResult.error("机构账号创建失败");
    }

    /**
     * 批量导入机构账号
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:batchImport')")
    @Log(title = "批量导入机构账号", businessType = BusinessType.IMPORT)
    @PostMapping("/batch-import")
    public AjaxResult batchImport(@RequestParam("file") MultipartFile file)
    {
        if (file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }

        try {
            // 解析Excel文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            List<Map<String, Object>> successList = new ArrayList<>();
            List<Map<String, Object>> failList = new ArrayList<>();
            int totalCount = 0;

            // 从第2行开始读取数据(第1行是表头)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalCount++;

                try {
                    // 读取Excel数据
                    String institutionName = getCellValue(row.getCell(0));
                    String creditCode = getCellValue(row.getCell(1));
                    String contactPerson = getCellValue(row.getCell(2));
                    String contactPhone = getCellValue(row.getCell(3));
                    String registeredCapital = getCellValue(row.getCell(4));
                    String registeredAddress = getCellValue(row.getCell(5));
                    String actualAddress = getCellValue(row.getCell(6));
                    String legalPerson = getCellValue(row.getCell(7));
                    String contactEmail = getCellValue(row.getCell(8));
                    String bedCount = getCellValue(row.getCell(9));

                    // 验证必填项
                    if (institutionName == null || institutionName.trim().isEmpty()) {
                        failList.add(createFailRecord(i, institutionName, "机构名称不能为空"));
                        continue;
                    }
                    if (creditCode == null || creditCode.trim().isEmpty()) {
                        failList.add(createFailRecord(i, institutionName, "统一信用代码不能为空"));
                        continue;
                    }
                    if (contactPerson == null || contactPerson.trim().isEmpty()) {
                        failList.add(createFailRecord(i, institutionName, "负责人姓名不能为空"));
                        continue;
                    }
                    if (contactPhone == null || contactPhone.trim().isEmpty()) {
                        failList.add(createFailRecord(i, institutionName, "联系电话不能为空"));
                        continue;
                    }

                    // 验证信用代码唯一性
                    PensionInstitution query = new PensionInstitution();
                    query.setCreditCode(creditCode);
                    List<PensionInstitution> existList = pensionInstitutionService.selectPensionInstitutionList(query);
                    if (existList != null && !existList.isEmpty()) {
                        failList.add(createFailRecord(i, institutionName, "统一信用代码已存在"));
                        continue;
                    }

                    // 构建机构对象
                    PensionInstitution institution = new PensionInstitution();
                    institution.setInstitutionName(institutionName);
                    institution.setCreditCode(creditCode);
                    institution.setContactPerson(contactPerson);
                    institution.setContactPhone(contactPhone);
                    institution.setLegalPerson(legalPerson);
                    institution.setContactEmail(contactEmail);

                    // 处理可选字段
                    if (registeredCapital != null && !registeredCapital.isEmpty()) {
                        try {
                            institution.setRegisteredCapital(Double.parseDouble(registeredCapital));
                        } catch (NumberFormatException e) {
                            // 忽略格式错误
                        }
                    }
                    if (bedCount != null && !bedCount.isEmpty()) {
                        try {
                            institution.setBedCount(Long.parseLong(bedCount));
                        } catch (NumberFormatException e) {
                            // 忽略格式错误
                        }
                    }
                    institution.setRegisteredAddress(registeredAddress);
                    institution.setActualAddress(actualAddress);

                    // 设置状态为草稿(4)
                    institution.setStatus("4");
                    institution.setCreateBy(getUsername());
                    institution.setCreateTime(new Date());

                    // 保存机构信息
                    int result = pensionInstitutionService.insertPensionInstitution(institution);

                    if (result > 0 && institution.getInstitutionId() != null) {
                        // 生成账号
                        Map<String, String> accountInfo = createInstitutionUser(
                            institution.getInstitutionId(),
                            institutionName,
                            contactPerson,
                            contactPhone
                        );

                        // 记录成功信息
                        Map<String, Object> successRecord = new HashMap<>();
                        successRecord.put("row", i);
                        successRecord.put("institutionName", institutionName);
                        successRecord.put("creditCode", creditCode);
                        successRecord.put("contactPerson", contactPerson);
                        successRecord.put("contactPhone", contactPhone);
                        successRecord.put("userName", accountInfo.get("userName"));
                        successRecord.put("password", accountInfo.get("password"));
                        successList.add(successRecord);
                    } else {
                        failList.add(createFailRecord(i, institutionName, "保存机构信息失败"));
                    }

                } catch (Exception e) {
                    failList.add(createFailRecord(i, "", "处理失败: " + e.getMessage()));
                    logger.error("导入第{}行数据失败", i, e);
                }
            }

            workbook.close();

            // 返回导入结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", totalCount);
            result.put("successCount", successList.size());
            result.put("failCount", failList.size());
            result.put("successList", successList);
            result.put("failList", failList);

            return AjaxResult.success(result);

        } catch (IOException e) {
            logger.error("批量导入机构账号失败", e);
            return AjaxResult.error("文件解析失败: " + e.getMessage());
        }
    }

    /**
     * 下载Excel导入模板
     */
    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response)
    {
        try {
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("机构导入模板");

            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "机构名称*", "统一信用代码*", "负责人姓名*", "联系电话*",
                "注册资金", "注册地址", "实际地址", "法定代表人",
                "联系邮箱", "床位数"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }

            // 创建示例数据行
            Row exampleRow = sheet.createRow(1);
            String[] examples = {
                "幸福养老院", "91410100MA44XXXX01", "张三", "13800138000",
                "500", "郑州市金水区XX路XX号", "郑州市金水区YY路YY号", "李四",
                "zhangsan@example.com", "100"
            };

            for (int i = 0; i < examples.length; i++) {
                exampleRow.createCell(i).setCellValue(examples[i]);
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("机构导入模板.xlsx", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);

            // 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (IOException e) {
            logger.error("下载模板失败", e);
        }
    }

    /**
     * 修改机构基本信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:edit')")
    @Log(title = "修改机构信息", businessType = BusinessType.UPDATE)
    @PutMapping("/account/edit")
    public AjaxResult editInstitutionAccount(@RequestBody PensionInstitution pensionInstitution)
    {
        pensionInstitution.setUpdateBy(getUsername());
        pensionInstitution.setUpdateTime(new Date());
        return toAjax(pensionInstitutionService.updatePensionInstitution(pensionInstitution));
    }

    /**
     * 重置机构管理员密码
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:edit')")
    @Log(title = "重置机构密码", businessType = BusinessType.UPDATE)
    @PutMapping("/account/resetPassword/{institutionId}")
    public AjaxResult resetPassword(@PathVariable Long institutionId)
    {
        try {
            // 查询关联的用户
            String sql = "SELECT user_id FROM sys_user_institution WHERE institution_id = ? LIMIT 1";
            Long userId = jdbcTemplate.queryForObject(sql, Long.class, institutionId);

            if (userId == null) {
                return AjaxResult.error("未找到关联的用户账号");
            }

            // 重置密码为123456
            String newPassword = "123456";
            String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);

            String updateSql = "UPDATE sys_user SET password = ?, update_by = ?, update_time = ? WHERE user_id = ?";
            jdbcTemplate.update(updateSql, encryptedPassword, getUsername(), new Date(), userId);

            Map<String, Object> result = new HashMap<>();
            result.put("newPassword", newPassword);
            result.put("message", "密码重置成功");

            return AjaxResult.success(result);

        } catch (Exception e) {
            logger.error("重置密码失败", e);
            return AjaxResult.error("重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 删除机构账号
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:remove')")
    @Log(title = "删除机构账号", businessType = BusinessType.DELETE)
    @DeleteMapping("/account/{institutionIds}")
    public AjaxResult removeInstitutionAccount(@PathVariable Long[] institutionIds)
    {
        // 删除机构时,关联的用户也会被级联删除(外键约束)
        return toAjax(pensionInstitutionService.deletePensionInstitutionByInstitutionIds(institutionIds));
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 创建机构管理员用户
     */
    private Map<String, String> createInstitutionUser(Long institutionId, String institutionName,
                                                      String contactPerson, String contactPhone)
    {
        // 生成用户名: jg + 电话后6位
        String userName = "jg" + contactPhone.substring(contactPhone.length() - 6);

        // 检查用户名是否已存在,如果存在则加随机数
        int suffix = 1;
        String finalUserName = userName;
        while (userService.selectUserByUserName(finalUserName) != null) {
            finalUserName = userName + suffix;
            suffix++;
        }

        // 初始密码:联系电话后6位
        String password = contactPhone.substring(contactPhone.length() - 6);

        // 创建用户对象
        SysUser user = new SysUser();
        user.setUserName(finalUserName);
        user.setNickName(institutionName + "-" + contactPerson);
        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setPhonenumber(contactPhone);
        user.setStatus("0"); // 正常状态
        user.setCreateBy(getUsername());
        user.setCreateTime(new Date());

        // 设置角色ID(机构管理员)
        Long[] roleIds = new Long[]{INSTITUTION_ADMIN_ROLE_ID};
        user.setRoleIds(roleIds);

        // 保存用户
        userService.insertUser(user);

        // 建立用户-机构关联
        String sql = "INSERT INTO sys_user_institution (user_id, institution_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, user.getUserId(), institutionId);

        logger.info("创建机构管理员账号: userId={}, userName={}, institutionId={}",
                   user.getUserId(), finalUserName, institutionId);

        // 返回账号信息
        Map<String, String> accountInfo = new HashMap<>();
        accountInfo.put("userName", finalUserName);
        accountInfo.put("password", password);
        accountInfo.put("userId", user.getUserId().toString());

        return accountInfo;
    }

    /**
     * 获取Excel单元格值
     */
    private String getCellValue(Cell cell)
    {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // 处理数字类型,避免科学计数法
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    return String.valueOf((long) value);
                } else {
                    return String.valueOf(value);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 创建失败记录
     */
    private Map<String, Object> createFailRecord(int row, String institutionName, String reason)
    {
        Map<String, Object> failRecord = new HashMap<>();
        failRecord.put("row", row);
        failRecord.put("institutionName", institutionName);
        failRecord.put("reason", reason);
        return failRecord;
      }

    /**
     * 查询已审核通过的机构列表（用于评级选择）
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating')")
    @GetMapping("/account/approved/list")
    public TableDataInfo listApprovedInstitutions(PensionInstitution pensionInstitution)
    {
        startPage();
        // 只查询已审核通过的机构(status='1')
        pensionInstitution.setStatus("1");
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);

        // 转换为前端期望的格式
        List<Map<String, Object>> convertedList = list.stream().map(institution -> {
            Map<String, Object> item = new HashMap<>();
            item.put("institutionId", institution.getInstitutionId());
            item.put("institutionName", institution.getInstitutionName());
            item.put("creditCode", institution.getCreditCode());
            return item;
        }).collect(Collectors.toList());

        TableDataInfo dataTable = getDataTable(list);
        dataTable.setRows(convertedList);
        return dataTable;
    }

    // ========== 机构评级管理 ==========

    /**
     * 查询机构评级列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating')")
    @GetMapping("/rating/list")
    public TableDataInfo listRating(InstitutionRating rating)
    {
        startPage();
        List<InstitutionRating> list = ratingService.selectInstitutionRatingList(rating);
        return getDataTable(list);
    }

    /**
     * 获取机构评级详细信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating')")
    @GetMapping("/rating/{ratingId}")
    public AjaxResult getRating(@PathVariable("ratingId") Long ratingId)
    {
        return success(ratingService.selectInstitutionRatingByRatingId(ratingId));
    }

    /**
     * 新增机构评级
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating:add')")
    @Log(title = "新增机构评级", businessType = BusinessType.INSERT)
    @PostMapping("/rating/add")
    public AjaxResult addRating(@RequestBody InstitutionRating rating)
    {
        rating.setCreateBy(getUsername());
        return toAjax(ratingService.insertInstitutionRating(rating));
    }

    /**
     * 修改机构评级
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating:edit')")
    @Log(title = "修改机构评级", businessType = BusinessType.UPDATE)
    @PutMapping("/rating/update")
    public AjaxResult updateRating(@RequestBody InstitutionRating rating)
    {
        rating.setUpdateBy(getUsername());
        return toAjax(ratingService.updateInstitutionRating(rating));
    }

    /**
     * 删除机构评级
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating:remove')")
    @Log(title = "删除机构评级", businessType = BusinessType.DELETE)
    @DeleteMapping("/rating/{ratingIds}")
    public AjaxResult delRating(@PathVariable Long[] ratingIds)
    {
        return toAjax(ratingService.deleteInstitutionRatingByRatingIds(ratingIds));
    }

    /**
     * 导出机构评级数据
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating:export')")
    @Log(title = "导出机构评级", businessType = BusinessType.EXPORT)
    @PostMapping("/rating/export")
    public void exportRating(HttpServletResponse response, InstitutionRating rating)
    {
        List<InstitutionRating> list = ratingService.selectInstitutionRatingList(rating);
        ExcelUtil<InstitutionRating> util = new ExcelUtil<InstitutionRating>(InstitutionRating.class);
        util.exportExcel(response, list, "机构评级数据");
    }

    /**
     * 下载机构评级导入模板
     */
    @PostMapping("/rating/template")
    public void downloadRatingTemplate(HttpServletResponse response)
    {
        ExcelUtil<InstitutionRating> util = new ExcelUtil<InstitutionRating>(InstitutionRating.class);
        util.importTemplateExcel(response, "机构评级导入模板");
    }

    /**
     * ���入机构评级数据
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:rating:import')")
    @Log(title = "导入机构评级", businessType = BusinessType.IMPORT)
    @PostMapping("/rating/import")
    public AjaxResult importRating(@RequestParam("file") MultipartFile file)
    {
        if (file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }

        try {
            List<InstitutionRating> successList = new ArrayList<>();
            List<Map<String, Object>> failList = new ArrayList<>();
            int totalCount = 0;

            // 使用ExcelUtil解析Excel
            ExcelUtil<InstitutionRating> util = new ExcelUtil<InstitutionRating>(InstitutionRating.class);
            List<InstitutionRating> ratingList = util.importExcel(file.getInputStream());

            totalCount = ratingList.size();

            for (int i = 0; i < ratingList.size(); i++) {
                InstitutionRating rating = ratingList.get(i);
                int rowNum = i + 2; // Excel行号从2开始（第1行是表头）

                try {
                    // 校验必填字段
                    if (rating.getInstitutionName() == null || rating.getInstitutionName().trim().isEmpty()) {
                        failList.add(createFailRecord(rowNum, rating.getInstitutionName(), "机构名称不能为空"));
                        continue;
                    }
                    if (rating.getCreditCode() == null || rating.getCreditCode().trim().isEmpty()) {
                        failList.add(createFailRecord(rowNum, rating.getInstitutionName(), "统一信用代码不能为空"));
                        continue;
                    }

                    // 通过统一信用代码查找机构ID
                    Long institutionId = null;
                    try {
                        institutionId = jdbcTemplate.queryForObject(
                            "SELECT institution_id FROM pension_institution WHERE credit_code = ?",
                            Long.class,
                            rating.getCreditCode().trim()
                        );
                    } catch (Exception e) {
                        // 忽略
                    }

                    if (institutionId == null) {
                        failList.add(createFailRecord(rowNum, rating.getInstitutionName(), "机构不存在，请先在系统中创建该机构"));
                        continue;
                    }

                    // 设置机构ID
                    rating.setInstitutionId(institutionId);

                    // 校验评级等级
                    if (rating.getRatingLevel() == null || rating.getRatingLevel() < 1 || rating.getRatingLevel() > 5) {
                        failList.add(createFailRecord(rowNum, rating.getInstitutionName(), "评级等级必须在1-5之间"));
                        continue;
                    }

                    // 校验总分
                    if (rating.getTotalScore() == null) {
                        failList.add(createFailRecord(rowNum, rating.getInstitutionName(), "总分不能为空"));
                        continue;
                    }

                    // 校验各项得分
                    if (rating.getServiceScore() == null) rating.setServiceScore(new java.math.BigDecimal("0"));
                    if (rating.getFacilityScore() == null) rating.setFacilityScore(new java.math.BigDecimal("0"));
                    if (rating.getManagementScore() == null) rating.setManagementScore(new java.math.BigDecimal("0"));
                    if (rating.getSafetyScore() == null) rating.setSafetyScore(new java.math.BigDecimal("0"));

                    // 设置默认值
                    if (rating.getValidityPeriod() == null) rating.setValidityPeriod(12);
                    if (rating.getRatingStatus() == null) rating.setRatingStatus("1");

                    // 自动计算有效期至
                    if (rating.getRatingDate() != null && rating.getValidityPeriod() != null) {
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.setTime(rating.getRatingDate());
                        cal.add(java.util.Calendar.MONTH, rating.getValidityPeriod());
                        rating.setExpireDate(cal.getTime());
                    }

                    // 设置创建信息
                    rating.setCreateBy(getUsername());

                    // 保存评级数据
                    ratingService.insertInstitutionRating(rating);

                    // 添加到成功列表
                    Map<String, Object> successRecord = new HashMap<>();
                    successRecord.put("row", rowNum);
                    successRecord.put("institutionName", rating.getInstitutionName());
                    successRecord.put("creditCode", rating.getCreditCode());
                    successRecord.put("ratingLevel", rating.getRatingLevel() + "星");
                    successRecord.put("totalScore", rating.getTotalScore());
                    successList.add(successRecord);

                } catch (Exception e) {
                    failList.add(createFailRecord(rowNum, rating.getInstitutionName(), "处理失败: " + e.getMessage()));
                    logger.error("导入第{}行数据失败", rowNum, e);
                }
            }

            // 返回导入结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", totalCount);
            result.put("successCount", successList.size());
            result.put("failCount", failList.size());
            result.put("successList", successList);
            result.put("failList", failList);

            return AjaxResult.success(result);

        } catch (Exception e) {
            logger.error("导入机构评级失败", e);
            return AjaxResult.error("文件解析失败: " + e.getMessage());
        }
    }

    /**
     * 获取机构评级信息（无需权限，用于前端展示）
     */
    @GetMapping("/rating/info/{institutionId}")
    public AjaxResult getRatingByInstitutionId(@PathVariable("institutionId") Long institutionId)
    {
        InstitutionRating rating = ratingService.selectLatestValidRatingByInstitutionId(institutionId);
        return success(rating);
    }

    // ========== 黑名单管理 ==========

    /**
     * 将机构加入黑名单
     */
    @PreAuthorize("@ss.hasPermi('pension:institution:blacklist')")
    @Log(title = "加入黑名单", businessType = BusinessType.UPDATE)
    @PostMapping("/addToBlacklist")
    public AjaxResult addToBlacklist(@RequestBody Map<String, Object> params)
    {
        Long[] institutionIds = null;
        String blacklistType = (String) params.get("blacklistType");
        String reason = (String) params.get("reason");

        Object idsObj = params.get("institutionIds");
        if (idsObj instanceof List) {
            List<?> idsList = (List<?>) idsObj;
            institutionIds = new Long[idsList.size()];
            for (int i = 0; i < idsList.size(); i++) {
                Object id = idsList.get(i);
                if (id instanceof Integer) {
                    institutionIds[i] = ((Integer) id).longValue();
                } else if (id instanceof Long) {
                    institutionIds[i] = (Long) id;
                } else if (id instanceof String) {
                    institutionIds[i] = Long.parseLong((String) id);
                }
            }
        } else if (idsObj instanceof Long[]) {
            institutionIds = (Long[]) idsObj;
        } else if (idsObj instanceof Integer[]) {
            Integer[] ids = (Integer[]) idsObj;
            institutionIds = new Long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                institutionIds[i] = ids[i].longValue();
            }
        } else if (idsObj instanceof Long) {
            institutionIds = new Long[]{(Long) idsObj};
        } else if (idsObj instanceof Integer) {
            institutionIds = new Long[]{((Integer) idsObj).longValue()};
        }

        if (institutionIds == null || institutionIds.length == 0) {
            return AjaxResult.error("请选择要加入黑名单的机构");
        }

        if (reason == null || reason.trim().isEmpty()) {
            return AjaxResult.error("请输入原因描述");
        }

        int count = institutionBlacklistService.addToBlacklist(institutionIds, blacklistType, reason);

        return AjaxResult.success("成功将 " + count + " 个机构加入黑名单");
    }
}
