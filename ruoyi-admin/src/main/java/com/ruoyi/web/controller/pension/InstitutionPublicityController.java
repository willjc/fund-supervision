package com.ruoyi.web.controller.pension;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.domain.InstitutionRating;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.service.IInstitutionRatingService;

/**
 * 机构公示信息Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/pension/publicity")
public class InstitutionPublicityController extends BaseController
{
    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    @Autowired
    private IPensionInstitutionPublicService pensionInstitutionPublicService;

    @Autowired
    private IInstitutionRatingService institutionRatingService;

    /**
     * 获取机构公示信息
     * 临时返回第一个机构的示例数据，实际应用中应该根据当前登录机构获取对应数据
     */
    @GetMapping("/info")
    public AjaxResult getPublicityInfo()
    {
        try {
            // 获取第一个正常运营的机构作为示例
            PensionInstitution queryCondition = new PensionInstitution();
            queryCondition.setStatus("1"); // 正常运营
            List<PensionInstitution> institutions = pensionInstitutionService.selectPensionInstitutionList(queryCondition);

            if (institutions == null || institutions.isEmpty()) {
                return error("暂无机构数据");
            }

            PensionInstitution institution = institutions.get(0);
            Map<String, Object> data = new HashMap<>();

            // 基本信息映射
            data.put("institutionId", institution.getInstitutionId());
            data.put("institutionName", institution.getInstitutionName());
            data.put("creditCode", institution.getCreditCode());
            data.put("legalPerson", institution.getLegalPerson());
            data.put("contactPhone", institution.getContactPhone());
            data.put("address", institution.getActualAddress());
            data.put("approvedBeds", institution.getBedCount());

            // 服务项目（暂时使用默认值，后续可扩展）
            data.put("serviceItems", Arrays.asList("生活照料", "医疗护理", "康复训练"));

            // 结构化费用数据
            Map<String, Object> nursingFee = new HashMap<>();
            nursingFee.put("min", institution.getNursingFeeMin() != null ? institution.getNursingFeeMin().doubleValue() : 1500.0);
            nursingFee.put("max", institution.getNursingFeeMax() != null ? institution.getNursingFeeMax().doubleValue() : 3500.0);

            Map<String, Object> bedFee = new HashMap<>();
            bedFee.put("min", institution.getBedFeeMin() != null ? institution.getBedFeeMin().doubleValue() : 800.0);
            bedFee.put("max", institution.getBedFeeMax() != null ? institution.getBedFeeMax().doubleValue() : 1500.0);

            Map<String, Object> mealFee = new HashMap<>();
            mealFee.put("min", institution.getMealFeeMin() != null ? institution.getMealFeeMin().doubleValue() : 600.0);
            mealFee.put("max", institution.getMealFeeMax() != null ? institution.getMealFeeMax().doubleValue() : 800.0);

            data.put("nursingFee", nursingFee);
            data.put("bedFee", bedFee);
            data.put("mealFee", mealFee);

            // 计算总费用
            Double totalMin = (Double) nursingFee.get("min") + (Double) bedFee.get("min") + (Double) mealFee.get("min");
            Double totalMax = (Double) nursingFee.get("max") + (Double) bedFee.get("max") + (Double) mealFee.get("max");
            Map<String, Object> totalFee = new HashMap<>();
            totalFee.put("min", totalMin);
            totalFee.put("max", totalMax);
            data.put("totalFee", totalFee);

            // 机构图片（JSON格式存储）
            data.put("coverImages", institution.getCoverImages());

            // 查询当前有效评级信息
            InstitutionRating validRating = institutionRatingService.selectLatestValidRatingByInstitutionId(institution.getInstitutionId());
            if (validRating != null) {
                data.put("ratingLevel", validRating.getRatingLevel());
                data.put("totalScore", validRating.getTotalScore());
                data.put("ratingDate", validRating.getRatingDate());
                data.put("expireDate", validRating.getExpireDate());
            } else {
                // 如果没有评��记录，使用机构表的默认评级
                data.put("ratingLevel", institution.getRatingLevel() != null ? institution.getRatingLevel() : 3);
                data.put("totalScore", null);
                data.put("ratingDate", null);
                data.put("expireDate", null);
            }

            // 查询公示信息表数据
            PensionInstitutionPublic publicity = pensionInstitutionPublicService.selectPensionInstitutionPublicByInstitutionId(institution.getInstitutionId());
            if (publicity != null) {
                // 设施图片
                data.put("roomFacilities", publicity.getRoomFacilities());
                data.put("basicFacilities", publicity.getBasicFacilities());
                data.put("parkFacilities", publicity.getParkFacilities());
                // VR全景图片
                data.put("vrImage", publicity.getVrImage());
                // 生活设施和医疗设施
                data.put("lifeFacilities", publicity.getLifeFacilities());
                data.put("medicalFacilities", publicity.getMedicalFacilities());
                // 每日服务安排
                data.put("dailyServices", publicity.getDailyServices());
            } else {
                data.put("roomFacilities", null);
                data.put("basicFacilities", null);
                data.put("parkFacilities", null);
                data.put("vrImage", null);
                data.put("lifeFacilities", null);
                data.put("medicalFacilities", null);
                data.put("dailyServices", null);
            }

            // 其他信息暂时使用默认值
            data.put("actualElders", 168);
            data.put("businessLicenseExpiry", "2026-12-31");
            data.put("foodLicenseExpiry", "2025-12-31");
            data.put("doctorCount", 5);
            data.put("nurseCount", 15);
            data.put("caregiverCount", 30);
            data.put("facilities", "配备医疗器械、康复器材、无障碍设施等");
            data.put("isPublic", true);
            data.put("publicNotice", "本机构为民政部门登记备案的合法养老服务机构");

            return success(data);
        } catch (Exception e) {
            logger.error("获取机构公示信息失败", e);
            return error("获取机构公示信息失败：" + e.getMessage());
        }
    }

    /**
     * 保存机构公示信息
     */
    @PostMapping("/save")
    public AjaxResult savePublicityInfo(@RequestBody Map<String, Object> params)
    {
        try {
            Long institutionId = Long.valueOf(params.get("institutionId").toString());

            // 查询现有机构信息
            PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
            if (institution == null) {
                return error("机构不存在");
            }

            // 解析并更新费用信息
            if (params.containsKey("nursingFee")) {
                Map<String, Object> nursingFee = (Map<String, Object>) params.get("nursingFee");
                institution.setNursingFeeMin(nursingFee.get("min") != null ?
                    java.math.BigDecimal.valueOf(Double.parseDouble(nursingFee.get("min").toString())) : null);
                institution.setNursingFeeMax(nursingFee.get("max") != null ?
                    java.math.BigDecimal.valueOf(Double.parseDouble(nursingFee.get("max").toString())) : null);
            }

            if (params.containsKey("bedFee")) {
                Map<String, Object> bedFee = (Map<String, Object>) params.get("bedFee");
                institution.setBedFeeMin(bedFee.get("min") != null ?
                    java.math.BigDecimal.valueOf(Double.parseDouble(bedFee.get("min").toString())) : null);
                institution.setBedFeeMax(bedFee.get("max") != null ?
                    java.math.BigDecimal.valueOf(Double.parseDouble(bedFee.get("max").toString())) : null);
            }

            if (params.containsKey("mealFee")) {
                Map<String, Object> mealFee = (Map<String, Object>) params.get("mealFee");
                institution.setMealFeeMin(mealFee.get("min") != null ?
                    java.math.BigDecimal.valueOf(Double.parseDouble(mealFee.get("min").toString())) : null);
                institution.setMealFeeMax(mealFee.get("max") != null ?
                    java.math.BigDecimal.valueOf(Double.parseDouble(mealFee.get("max").toString())) : null);
            }

            // 更新机构图片（JSON格式）
            if (params.containsKey("coverImages")) {
                String coverImages = params.get("coverImages") != null ? params.get("coverImages").toString() : null;
                institution.setCoverImages(coverImages);
            }

            // 更新其他基本信息
            if (params.containsKey("legalPerson")) {
                institution.setLegalPerson(params.get("legalPerson").toString());
            }
            if (params.containsKey("contactPhone")) {
                institution.setContactPhone(params.get("contactPhone").toString());
            }
            if (params.containsKey("address")) {
                institution.setActualAddress(params.get("address").toString());
            }

            // 保存机构基础信息到数据库
            int result = pensionInstitutionService.updatePensionInstitution(institution);

            // 同时更新��示信息表（pension_institution_public）
            PensionInstitutionPublic publicity = pensionInstitutionPublicService.selectPensionInstitutionPublicByInstitutionId(institutionId);

            if (publicity == null) {
                // 如果不存在公示信息，创建新记录
                publicity = new PensionInstitutionPublic();
                publicity.setInstitutionId(institutionId);
            }

            // 更新设施图片
            if (params.containsKey("roomFacilities")) {
                publicity.setRoomFacilities(params.get("roomFacilities") != null ? params.get("roomFacilities").toString() : null);
            }
            if (params.containsKey("basicFacilities")) {
                publicity.setBasicFacilities(params.get("basicFacilities") != null ? params.get("basicFacilities").toString() : null);
            }
            if (params.containsKey("parkFacilities")) {
                publicity.setParkFacilities(params.get("parkFacilities") != null ? params.get("parkFacilities").toString() : null);
            }

            // 更新VR全景图片
            if (params.containsKey("vrImage")) {
                publicity.setVrImage(params.get("vrImage") != null ? params.get("vrImage").toString() : null);
            }

            // 更新生活设施和医疗设施
            if (params.containsKey("lifeFacilities")) {
                publicity.setLifeFacilities(params.get("lifeFacilities") != null ? params.get("lifeFacilities").toString() : null);
            }
            if (params.containsKey("medicalFacilities")) {
                publicity.setMedicalFacilities(params.get("medicalFacilities") != null ? params.get("medicalFacilities").toString() : null);
            }

            // 更新每日服务安排
            if (params.containsKey("dailyServices")) {
                publicity.setDailyServices(params.get("dailyServices") != null ? params.get("dailyServices").toString() : null);
            }

            // 保存公示信息到数据库
            int publicityResult;
            if (publicity.getPublicId() != null) {
                publicityResult = pensionInstitutionPublicService.updatePensionInstitutionPublic(publicity);
            } else {
                publicityResult = pensionInstitutionPublicService.insertPensionInstitutionPublic(publicity);
            }

            if (result > 0 && publicityResult > 0) {
                return success("公示信息保存成功");
            } else {
                return error("保存失败");
            }
        } catch (Exception e) {
            logger.error("保存机构公示信息失败", e);
            return error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 上传公示图片（已存在文件上传功能，这里只做URL记录）
     */
    @PostMapping("/upload-picture")
    public AjaxResult uploadPicture(@RequestParam("file") String fileUrl)
    {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("name", "环境图片");
            return success(data);
        } catch (Exception e) {
            logger.error("图片上传失败", e);
            return error("图片上传失败：" + e.getMessage());
        }
    }
}
