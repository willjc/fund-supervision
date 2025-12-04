package com.ruoyi.web.controller.h5;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.service.IBedInfoService;

/**
 * H5养老机构Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/institution")
public class H5InstitutionController extends BaseController
{
    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    @Autowired
    private IPensionInstitutionPublicService pensionInstitutionPublicService;

    @Autowired
    private IBedInfoService bedInfoService;

    /**
     * 查询养老机构列表 (H5端,不需要权限)
     * 支持区域街道多选筛选，优先使用公示数据，与首页推荐接口保持一致
     */
    @GetMapping("/list")
    public TableDataInfo list(PensionInstitution pensionInstitution,
                             @RequestParam(required = false) List<String> areaCodes,
                             @RequestParam(required = false) List<String> streetNames)
    {
        startPage();

        // 设置多选筛选参数
        if (areaCodes != null && !areaCodes.isEmpty()) {
            pensionInstitution.setAreaCodes(areaCodes);
        }
        if (streetNames != null && !streetNames.isEmpty()) {
            pensionInstitution.setStreetNames(streetNames);
        }

        // H5端只显示审核通过且正常运营的机构
        pensionInstitution.setStatus("1");  // 1-正常运营
        List<PensionInstitution> institutionList = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);

        // 获取机构ID列表，用于查询公示信息
        List<Long> institutionIds = institutionList.stream()
                .map(PensionInstitution::getInstitutionId)
                .collect(Collectors.toList());

        // 查询这些机构的公示信息
        List<PensionInstitutionPublic> publicityList = new ArrayList<>();
        if (!institutionIds.isEmpty()) {
            PensionInstitutionPublic queryPublicity = new PensionInstitutionPublic();
            queryPublicity.setIsPublished("1");  // 只查询已发布的公示信息
            publicityList = pensionInstitutionPublicService.selectPensionInstitutionPublicList(queryPublicity)
                    .stream()
                    .filter(publicity -> publicity.getInstitutionId() != null && institutionIds.contains(publicity.getInstitutionId()))
                    .collect(Collectors.toList());
        }

        // 创建institutionId到公示信息的映射
        Map<Long, PensionInstitutionPublic> publicityMap = publicityList.stream()
                .collect(Collectors.toMap(
                        PensionInstitutionPublic::getInstitutionId,
                        publicity -> publicity,
                        (existing, replacement) -> existing  // 如果有重复，保留第一个
                ));

        // 创建institutionId到机构信息的映射
        Map<Long, PensionInstitution> institutionMap = institutionList.stream()
                .collect(Collectors.toMap(
                        PensionInstitution::getInstitutionId,
                        institution -> institution,
                        (existing, replacement) -> existing  // 如果有重复，保留第一个
                ));

        // 优先使用公示数据，转换为H5前端期望的格式
        List<Map<String, Object>> convertedList = institutionList.stream()
                .map(institution -> {
                    // 如果有公示信息，使用公示数据；否则使用基础数据
                    if (publicityMap.containsKey(institution.getInstitutionId())) {
                        return convertPublicityToH5Format(publicityMap.get(institution.getInstitutionId()), institution);
                    } else {
                        return convertToH5Format(institution);
                    }
                })
                .collect(Collectors.toList());

        // 使用分页信息包装转换后的数据
        TableDataInfo dataTable = getDataTable(institutionList);
        dataTable.setRows(convertedList);  // 替换为转换后的数据

        return dataTable;
    }

    /**
     * 获取养老机构详细信息 (H5端)
     */
    @GetMapping("/{institutionId}")
    public AjaxResult getInfo(@PathVariable("institutionId") Long institutionId)
    {
        PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
        if (institution == null)
        {
            return AjaxResult.error("机构不存在");
        }
        // 只返回正常运营的机构
        if (!"1".equals(institution.getStatus()))
        {
            return AjaxResult.error("该机构暂不可用");
        }
        return AjaxResult.success(institution);
    }

    /**
     * 获取推荐机构列表 (首页展示)
     * 返回H5前端期望的数据格式，优先使用公示信息数据
     * 支持区域街道多选筛选
     */
    @GetMapping("/recommend")
    public AjaxResult getRecommendList(@RequestParam(required = false) List<String> areaCodes,
                                     @RequestParam(required = false) List<String> streetNames)
    {
        // 查询已发布的公示信息
        PensionInstitutionPublic queryPublicity = new PensionInstitutionPublic();
        queryPublicity.setIsPublished("1");  // 只查询已发布的公示信息

        // 设置多选筛选参数到关联的机构查询中
        if ((areaCodes != null && !areaCodes.isEmpty()) || (streetNames != null && !streetNames.isEmpty())) {
            // 如果有筛选条件，需要通过机构表筛选
            PensionInstitution institutionQuery = new PensionInstitution();
            institutionQuery.setStatus("1");  // 只查询正常运营的机构
            if (areaCodes != null && !areaCodes.isEmpty()) {
                institutionQuery.setAreaCodes(areaCodes);
            }
            if (streetNames != null && !streetNames.isEmpty()) {
                institutionQuery.setStreetNames(streetNames);
            }

            // 先查询符合筛选条件的机构
            List<PensionInstitution> filteredInstitutions = pensionInstitutionService.selectPensionInstitutionList(institutionQuery);
            if (filteredInstitutions.isEmpty()) {
                return AjaxResult.success(new ArrayList<>());  // 没有符合条件的机构
            }

            // 获取筛选后的机构ID列表
            List<Long> institutionIds = filteredInstitutions.stream()
                    .map(PensionInstitution::getInstitutionId)
                    .collect(Collectors.toList());

            // 查询这些机构的公示信息
            List<PensionInstitutionPublic> publicityList = pensionInstitutionPublicService.selectPensionInstitutionPublicList(queryPublicity)
                    .stream()
                    .filter(publicity -> publicity.getInstitutionId() != null && institutionIds.contains(publicity.getInstitutionId()))
                    .collect(Collectors.toList());

            // 转换为H5前端期望的格式
            List<Map<String, Object>> convertedList = publicityList.stream()
                    .map(this::convertPublicityToH5Format)
                    .limit(8)  // 限制返回数量
                    .collect(Collectors.toList());

            return AjaxResult.success(convertedList);
        } else {
            // 没有筛选条件，按原逻辑查询
            List<PensionInstitutionPublic> publicityList = pensionInstitutionPublicService.selectPensionInstitutionPublicList(queryPublicity);

            // 转换为H5前端期望的格式
            List<Map<String, Object>> convertedList = publicityList.stream()
                    .filter(publicity -> publicity.getInstitutionId() != null)  // 过滤掉没有机构ID的记录
                    .limit(8)  // 返回前8个机构，与mock数据数量一致
                    .map(this::convertPublicityToH5Format)
                    .collect(Collectors.toList());

            return AjaxResult.success(convertedList);
        }
    }

    /**
     * 将PensionInstitutionPublic实体转换为H5前端期望的数据格式（简化版，用于推荐接口）
     */
    private Map<String, Object> convertPublicityToH5Format(PensionInstitutionPublic publicity)
    {
        // 先通过机构ID获取基础机构信息
        PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(publicity.getInstitutionId());
        return convertPublicityToH5Format(publicity, institution);
    }

    /**
     * 将PensionInstitutionPublic实体转换为H5前端期望的数据格式（带机构信息）
     */
    private Map<String, Object> convertPublicityToH5Format(PensionInstitutionPublic publicity, PensionInstitution institution)
    {
        Map<String, Object> result = new HashMap<>();

        // 基础字段映射（下划线转驼峰）
        result.put("institutionId", publicity.getInstitutionId());
        // 使用基础机构信息中的机构名称
        result.put("institutionName", institution != null ? institution.getInstitutionName() : "未知机构");

        // 使用机构基础信息中的实际地址（公示表可能没有）
        String address = (institution != null && institution.getActualAddress() != null && !institution.getActualAddress().trim().isEmpty()) ?
                        institution.getActualAddress() : publicity.getActualAddress();
        result.put("address", address == null || address.trim().isEmpty() ?
                    "地址信息完善中" : address);

        // 使用机构基础信息中的联系电话（公示表可能没有）
        String contactPhone = (institution != null && institution.getContactPhone() != null && !institution.getContactPhone().trim().isEmpty()) ?
                             institution.getContactPhone() : publicity.getContactPhone();
        result.put("contactPhone", contactPhone != null ? contactPhone : "");

        // 使用公示表的床位数，如果没有则使用基础机构的床位数
        Integer bedCount = publicity.getBedCount();
        if (bedCount == null && institution != null) {
            // 处理类型转换：Long -> Integer
            Long institutionBedCount = institution.getBedCount();
            bedCount = institutionBedCount != null ? institutionBedCount.intValue() : null;
        }
        result.put("bedCount", bedCount != null ? bedCount : 0);
        result.put("institutionType", "nursing_home"); // 默认类型

        // 获取床位统计
        try {
            Map<String, Object> bedStatistics = bedInfoService.getBedStatistics(publicity.getInstitutionId());
            result.put("totalBeds", bedStatistics.get("totalBeds"));
            result.put("availableBeds", bedStatistics.get("availableBeds"));
        } catch (Exception e) {
            // 异常时使用0
            result.put("totalBeds", 0);
            result.put("availableBeds", 0);
        }

        // 费用区间构建 - 优先使用结构化费用数据，回退到简单费用区间
        Map<String, Object> priceRanges = new HashMap<>();

        // 1. 护理费 - 优先使用新的nursing_fee字段，否则使用原fee_range字段
        Double nursingMin = null, nursingMax = null;
        if (publicity.getNursingFeeMin() != null && publicity.getNursingFeeMax() != null) {
            nursingMin = publicity.getNursingFeeMin().doubleValue();
            nursingMax = publicity.getNursingFeeMax().doubleValue();
        } else if (publicity.getFeeRangeMin() != null && publicity.getFeeRangeMax() != null) {
            // 如果没有详细的护理费，将总费用按比例分配给护理费（60%）
            nursingMin = publicity.getFeeRangeMin().doubleValue() * 0.6;
            nursingMax = publicity.getFeeRangeMax().doubleValue() * 0.6;
        }

        // 2. 床位费 - 使用新的bed_fee字段，否则按护理费的30%计算
        Double bedMin = publicity.getBedFeeMin() != null ? publicity.getBedFeeMin().doubleValue() :
                      (nursingMin != null ? nursingMin * 0.5 : 800.0);
        Double bedMax = publicity.getBedFeeMax() != null ? publicity.getBedFeeMax().doubleValue() :
                      (nursingMax != null ? nursingMax * 0.5 : 1500.0);

        // 3. 膳食费 - 使用新的meal_fee字段，否则按护理费的20%计算
        Double mealMin = publicity.getMealFeeMin() != null ? publicity.getMealFeeMin().doubleValue() :
                      (nursingMin != null ? nursingMin * 0.3 : 600.0);
        Double mealMax = publicity.getMealFeeMax() != null ? publicity.getMealFeeMax().doubleValue() :
                      (nursingMax != null ? nursingMax * 0.3 : 800.0);

        // 如果仍然没有护理费数据，使用默认值
        if (nursingMin == null || nursingMax == null) {
            nursingMin = 1500.0;
            nursingMax = 3500.0;
            bedMin = 800.0;
            bedMax = 1500.0;
            mealMin = 600.0;
            mealMax = 800.0;
        }

        // 4. 总费用 = 护理费 + 床位费 + 膳食费
        Double totalMin = nursingMin + bedMin + mealMin;
        Double totalMax = nursingMax + bedMax + mealMax;

        // 5. 构建价格结构（按用户要求的顺序：总费用、床位费、护理费、膳食费）
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("min", totalMin);
        totalMap.put("max", totalMax);
        priceRanges.put("total", totalMap);

        Map<String, Object> bedMap = new HashMap<>();
        bedMap.put("min", bedMin);
        bedMap.put("max", bedMax);
        priceRanges.put("bed", bedMap);

        Map<String, Object> nursingMap = new HashMap<>();
        nursingMap.put("min", nursingMin);
        nursingMap.put("max", nursingMax);
        priceRanges.put("nursing", nursingMap);

        Map<String, Object> dietMap = new HashMap<>();
        dietMap.put("min", mealMin);
        dietMap.put("max", mealMax);
        priceRanges.put("diet", dietMap);

        result.put("priceRanges", priceRanges);

        // 6. 机构封面图片处理 - 优先使用主图，否则使用环境图片
        String coverImage = null;

        // 首先使用主图
        if (publicity.getMainPicture() != null && !publicity.getMainPicture().trim().isEmpty()) {
            coverImage = publicity.getMainPicture();
        }

        // 如果没有主图，尝试使用环境图片
        if (coverImage == null && publicity.getEnvironmentImgs() != null && !publicity.getEnvironmentImgs().trim().isEmpty()) {
            try {
                // 尝试解析JSON格式的图片URL列表，取第一张作为封面
                String[] imageUrls = publicity.getEnvironmentImgs().split(",");
                if (imageUrls.length > 0) {
                    coverImage = imageUrls[0].trim();
                }
            } catch (Exception e) {
                System.out.println("解析环境图片失败: " + publicity.getEnvironmentImgs());
            }
        }

        // 如果没有任何图片，使用默认图片
        if (coverImage == null || coverImage.trim().isEmpty()) {
            coverImage = "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg";
        }

        result.put("coverImage", coverImage);

        return result;
    }

    /**
     * 将PensionInstitution实体转换为H5前端期望的数据格式
     */
    private Map<String, Object> convertToH5Format(PensionInstitution institution)
    {
        Map<String, Object> result = new HashMap<>();

        // 基础字段映射（下划线转驼峰）
        result.put("institutionId", institution.getInstitutionId());
        result.put("institutionName", institution.getInstitutionName());
        result.put("address", institution.getActualAddress() == null || institution.getActualAddress().trim().isEmpty() ?
                    "地址信息完善中" : institution.getActualAddress());
        result.put("contactPhone", institution.getContactPhone() != null ? institution.getContactPhone() : "");
        // 处理类型转换：Long -> Integer
        Long institutionBedCount = institution.getBedCount();
        result.put("bedCount", institutionBedCount != null ? institutionBedCount.intValue() : 0);
        result.put("institutionType", convertInstitutionType(institution.getInstitutionType()));

        // 获取床位统计（与首页推荐接口保持一致）
        try {
            Map<String, Object> bedStatistics = bedInfoService.getBedStatistics(institution.getInstitutionId());
            result.put("totalBeds", bedStatistics.get("totalBeds"));
            result.put("availableBeds", bedStatistics.get("availableBeds"));
        } catch (Exception e) {
            // 异常时使用默认值
            Long totalBedCount = institution.getBedCount();
            result.put("totalBeds", totalBedCount != null ? totalBedCount.intValue() : 0);
            result.put("availableBeds", 0);
        }

        // 费用区间构建 - 使用真实数据库数据
        Map<String, Object> priceRanges = new HashMap<>();

        // 1. 护理费 - 优先使用新的nursing_fee字段，否则使用原price_range字段
        Double nursingMin = null, nursingMax = null;
        if (institution.getNursingFeeMin() != null && institution.getNursingFeeMax() != null) {
            nursingMin = institution.getNursingFeeMin().doubleValue();
            nursingMax = institution.getNursingFeeMax().doubleValue();
        } else if (institution.getPriceRangeMin() != null && institution.getPriceRangeMax() != null) {
            nursingMin = institution.getPriceRangeMin().doubleValue();
            nursingMax = institution.getPriceRangeMax().doubleValue();
        }

        // 如果仍然没有护理费数据，使用默认值
        if (nursingMin == null || nursingMax == null) {
            nursingMin = 1500.0;
            nursingMax = 3500.0;
        }

        // 2. 床位费 - 使用新的bed_fee字段
        Double bedMin = institution.getBedFeeMin() != null ? institution.getBedFeeMin().doubleValue() : Math.round(nursingMin * 0.3);
        Double bedMax = institution.getBedFeeMax() != null ? institution.getBedFeeMax().doubleValue() : Math.round(nursingMax * 0.3);

        // 3. 膳食费 - 使用新的meal_fee字段
        Double dietMin = institution.getMealFeeMin() != null ? institution.getMealFeeMin().doubleValue() : Math.round(nursingMin * 0.2);
        Double dietMax = institution.getMealFeeMax() != null ? institution.getMealFeeMax().doubleValue() : Math.round(nursingMax * 0.2);

        // 4. 总费用 = 护理费 + 床位费 + 膳食费
        Double totalMin = nursingMin + bedMin + dietMin;
        Double totalMax = nursingMax + bedMax + dietMax;

        // 5. 构建价格结构（按用户要求的顺序：总费用、床位费、护理费、膳食费）
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("min", totalMin);
        totalMap.put("max", totalMax);
        priceRanges.put("total", totalMap);

        Map<String, Object> bedMap = new HashMap<>();
        bedMap.put("min", bedMin);
        bedMap.put("max", bedMax);
        priceRanges.put("bed", bedMap);

        Map<String, Object> nursingMap = new HashMap<>();
        nursingMap.put("min", nursingMin);
        nursingMap.put("max", nursingMax);
        priceRanges.put("nursing", nursingMap);

        Map<String, Object> dietMap = new HashMap<>();
        dietMap.put("min", dietMin);
        dietMap.put("max", dietMax);
        priceRanges.put("diet", dietMap);

        result.put("priceRanges", priceRanges);

        // 6. 机构封面图片处理
        String coverImage = null;
        if (institution.getCoverImages() != null && !institution.getCoverImages().trim().isEmpty()) {
            try {
                // 尝试解析JSON格式的图片URL列表，取第一张作为封面
                String[] imageUrls = institution.getCoverImages().split(",");
                if (imageUrls.length > 0) {
                    coverImage = imageUrls[0].trim();
                }
            } catch (Exception e) {
                System.out.println("解析封面图片失败: " + institution.getCoverImages());
            }
        }

        // 如果没有封面图片，使用默认图片
        if (coverImage == null || coverImage.trim().isEmpty()) {
            coverImage = "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg";
        }

        result.put("coverImage", coverImage);

        return result;
    }

    /**
     * 转换机构类型代码为前端期望的格式
     */
    private String convertInstitutionType(String institutionType)
    {
        if (institutionType == null || institutionType.trim().isEmpty()) {
            return "nursing_home";
        }

        switch (institutionType) {
            case "1":
                return "nursing_home";  // 养老院
            case "2":
                return "care_center";   // 护理中心
            case "3":
                return "service_center"; // 社区养老服务中心
            default:
                return "nursing_home";
        }
    }

    /**
     * 解析fee_range文本，补充价格区间数据
     * fee_range格式可能为："2000" 或 "2000-3000"
     */
    private void parseFeeRangeToPriceRanges(String feeRange, Map<String, Object> priceRanges)
    {
        try {
            if (feeRange.contains("-")) {
                // 格式如 "2000-3000"
                String[] parts = feeRange.split("-");
                if (parts.length >= 2) {
                    Double min = Double.parseDouble(parts[0].trim());
                    Double max = Double.parseDouble(parts[1].trim());

                    // 将fee_range作为基础护理费的补充或验证
                    if (!priceRanges.containsKey("nursing")) {
                        Map<String, Object> nursingMap = new HashMap<>();
                        nursingMap.put("min", min);
                        nursingMap.put("max", max);
                        priceRanges.put("nursing", nursingMap);
                    }
                }
            } else {
                // 格式如 "2000"，作为基础护理费
                Double amount = Double.parseDouble(feeRange.trim());
                if (!priceRanges.containsKey("nursing")) {
                    Map<String, Object> nursingMap = new HashMap<>();
                    nursingMap.put("min", amount);
                    nursingMap.put("max", amount);
                    priceRanges.put("nursing", nursingMap);
                }
            }
        } catch (NumberFormatException e) {
            // 解析失败时忽略，使用默认值
            System.out.println("解析fee_range失败: " + feeRange);
        }
    }
}
