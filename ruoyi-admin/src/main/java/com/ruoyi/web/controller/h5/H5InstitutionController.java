package com.ruoyi.web.controller.h5;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.domain.InstitutionRating;
import com.ruoyi.domain.pension.PensionInstitutionAttach;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.service.IFacilityIconConfigService;
import com.ruoyi.service.IInstitutionRatingService;
import com.ruoyi.mapper.pension.PensionInstitutionAttachMapper;
import com.ruoyi.domain.FacilityIconConfig;
import com.ruoyi.web.controller.h5.vo.InstitutionImageCategoryVO;

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

    @Autowired
    private IFacilityIconConfigService facilityIconConfigService;

    @Autowired
    private IInstitutionRatingService ratingService;

    @Autowired
    private PensionInstitutionAttachMapper pensionInstitutionAttachMapper;

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

        // H5端只显示审核通过且正常运营的机构，且不在黑名单中
        pensionInstitution.setStatus("1");  // 1-正常运营
        pensionInstitution.setBlacklistFlag("0");  // 0-非黑名单
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

        // 只返回有已发布公示信息的机构（H5端仅展示已发布的公示机构）
        List<Map<String, Object>> convertedList = institutionList.stream()
                .filter(institution -> publicityMap.containsKey(institution.getInstitutionId()))  // 只保留有公示的机构
                .map(institution -> {
                    return convertPublicityToH5Format(publicityMap.get(institution.getInstitutionId()), institution);
                })
                .collect(Collectors.toList());

        // 使用分页信息包装转换后的数据
        TableDataInfo dataTable = getDataTable(institutionList);
        dataTable.setRows(convertedList);  // 替换为转换后的数据
        dataTable.setTotal(convertedList.size());  // 更新总数为过滤后的数量

        return dataTable;
    }

    /**
     * 获取养老机构详细信息 (H5端)
     * 返回格式化的机构详情数据，包含公示信息
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

        // 查询机构的公示信息
        PensionInstitutionPublic queryPublicity = new PensionInstitutionPublic();
        queryPublicity.setInstitutionId(institutionId);
        queryPublicity.setIsPublished("1");  // 只查询已发布的公示信息
        List<PensionInstitutionPublic> publicityList = pensionInstitutionPublicService.selectPensionInstitutionPublicList(queryPublicity);

        // 转换为H5详情页面需要的格式
        Map<String, Object> detailData = convertToH5DetailFormat(institution,
                publicityList.isEmpty() ? null : publicityList.get(0));

        return AjaxResult.success(detailData);
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
            institutionQuery.setBlacklistFlag("0");  // 0-非黑名单
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
            // 没有筛选条件，也需要过滤黑名单机构
            PensionInstitution institutionQuery = new PensionInstitution();
            institutionQuery.setStatus("1");  // 只查询正常运营的机构
            institutionQuery.setBlacklistFlag("0");  // 0-非黑名单

            // 先查询符合条件（非黑名单）的机构ID列表
            List<PensionInstitution> filteredInstitutions = pensionInstitutionService.selectPensionInstitutionList(institutionQuery);
            if (filteredInstitutions.isEmpty()) {
                return AjaxResult.success(new ArrayList<>());  // 没有符合条件的机构
            }

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
                    .limit(8)  // 返回前8个机构，与mock数据数量一致
                    .collect(Collectors.toList());

            return AjaxResult.success(convertedList);
        }
    }

    /**
     * 获取机构图片分类列表 (H5端)
     *
     * @param institutionId 机构ID
     * @return 图片分类列表
     */
    @GetMapping("/{institutionId}/images")
    public AjaxResult getInstitutionImages(@PathVariable("institutionId") Long institutionId)
    {
        List<Map<String, Object>> groupedAttachments = pensionInstitutionAttachMapper.selectAttachmentsGroupByType(institutionId);

        // 构建图片分类数据
        List<InstitutionImageCategoryVO> imageCategories = new ArrayList<>();

        // 判断attach表是否有数据
        boolean hasAttachData = groupedAttachments != null && !groupedAttachments.isEmpty();

        if (hasAttachData) {
            // 从attach表获取
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_VR, "VR", "vr");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_MAIN, "主图", "main");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_ENVIRONMENT, "环境", "environment");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_ROOM, "房间", "room");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_BASIC, "设施", "basic");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_PARK, "园址", "park");
        } else {
            // 从publicity表字段获取（兼容管理端上传的图片）
            // 查询机构的公示信息
            PensionInstitutionPublic queryPublicity = new PensionInstitutionPublic();
            queryPublicity.setInstitutionId(institutionId);
            queryPublicity.setIsPublished("1");
            List<PensionInstitutionPublic> publicityList = pensionInstitutionPublicService.selectPensionInstitutionPublicList(queryPublicity);
            PensionInstitutionPublic publicity = publicityList.isEmpty() ? null : publicityList.get(0);

            if (publicity != null) {
                imageCategories.add(new InstitutionImageCategoryVO("vr", "VR", parseCommaSeparatedImages(publicity.getVrImage())));
                imageCategories.add(new InstitutionImageCategoryVO("main", "主图", parseCommaSeparatedImages(publicity.getMainPicture())));
                imageCategories.add(new InstitutionImageCategoryVO("environment", "环境", parseCommaSeparatedImages(publicity.getEnvironmentImgs())));
                imageCategories.add(new InstitutionImageCategoryVO("room", "房间", parseCommaSeparatedImages(publicity.getRoomFacilities())));
                imageCategories.add(new InstitutionImageCategoryVO("basic", "设施", parseCommaSeparatedImages(publicity.getBasicFacilities())));
                imageCategories.add(new InstitutionImageCategoryVO("park", "园址", parseCommaSeparatedImages(publicity.getParkFacilities())));
            } else {
                // 没有任何图片数据
                imageCategories.add(new InstitutionImageCategoryVO("vr", "VR", new ArrayList<>()));
                imageCategories.add(new InstitutionImageCategoryVO("main", "主图", new ArrayList<>()));
                imageCategories.add(new InstitutionImageCategoryVO("environment", "环境", new ArrayList<>()));
                imageCategories.add(new InstitutionImageCategoryVO("room", "房间", new ArrayList<>()));
                imageCategories.add(new InstitutionImageCategoryVO("basic", "设施", new ArrayList<>()));
                imageCategories.add(new InstitutionImageCategoryVO("park", "园址", new ArrayList<>()));
            }
        }

        return AjaxResult.success(imageCategories);
    }

    /**
     * 添加图片分类
     */
    private void addImageCategory(List<InstitutionImageCategoryVO> categories,
                                  List<Map<String, Object>> groupedAttachments,
                                  String attachType, String name, String key)
    {
        List<String> images = new ArrayList<>();
        for (Map<String, Object> group : groupedAttachments) {
            if (attachType.equals(group.get("attachType"))) {
                String imagesStr = (String) group.get("images");
                if (imagesStr != null && !imagesStr.isEmpty()) {
                    String[] imageArray = imagesStr.split(",");
                    for (String image : imageArray) {
                        if (image != null && !image.trim().isEmpty()) {
                            images.add(image.trim());
                        }
                    }
                }
            }
        }
        categories.add(new InstitutionImageCategoryVO(key, name, images));
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
        // 机构类型从机��基础信息获取，用于前端筛选
        result.put("institutionType", institution != null ? convertInstitutionType(institution.getInstitutionType()) : "nursing_home");

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

        // 7. 生活设施 - 解析JSON数组
        List<String> lifeFacilitiesList = new ArrayList<>();
        if (publicity.getLifeFacilities() != null && !publicity.getLifeFacilities().trim().isEmpty()) {
            try {
                lifeFacilitiesList = JSON.parseArray(publicity.getLifeFacilities(), String.class);
            } catch (Exception e) {
                System.out.println("解析生活设施失败: " + publicity.getLifeFacilities());
            }
        }
        result.put("lifeFacilities", lifeFacilitiesList);

        // 添加收住类型（从机构基础信息获取，用于前端筛选）
        if (institution != null) {
            result.put("careLevels", institution.getCareLevels());
        }

        // 添加机构评级数据（用于前端星级筛选）
        try {
            InstitutionRating queryRating = new InstitutionRating();
            queryRating.setInstitutionId(institution != null ? institution.getInstitutionId() : null);
            List<InstitutionRating> ratings = ratingService.selectInstitutionRatingList(queryRating);

            if (!ratings.isEmpty()) {
                // 获取最新的有效评级
                InstitutionRating latestRating = ratings.stream()
                        .filter(r -> "1".equals(r.getRatingStatus())) // 只获取有效的评级
                        .sorted((a, b) -> b.getRatingDate().compareTo(a.getRatingDate())) // 按评级日期倒序
                        .findFirst()
                        .orElse(null);

                result.put("ratingLevel", latestRating != null ? latestRating.getRatingLevel() : 3);
            } else {
                result.put("ratingLevel", 3); // 默认3星
            }
        } catch (Exception e) {
            result.put("ratingLevel", 3); // 异常时使用默认值
            System.out.println("查询机构评级失败: " + e.getMessage());
        }

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

    /**
     * 转换为H5详情页面需要的格式
     */
    private Map<String, Object> convertToH5DetailFormat(PensionInstitution institution, PensionInstitutionPublic publicity) {
        Map<String, Object> result = new HashMap<>();

        // 基础信息
        result.put("id", institution.getInstitutionId()); // 添加id字段供前端使用
        result.put("institutionId", institution.getInstitutionId());
        result.put("name", institution.getInstitutionName());
        result.put("description", publicity != null ? publicity.getInstitutionIntro() : "");
        result.put("address", institution.getActualAddress());
        result.put("contactPhone", institution.getContactPhone());

        if (publicity != null) {
            // 价格信息
            Map<String, Object> priceRanges = new HashMap<>();

            Map<String, Object> nursingMap = new HashMap<>();
            nursingMap.put("min", publicity.getNursingFeeMin());
            nursingMap.put("max", publicity.getNursingFeeMax());
            priceRanges.put("nursing", nursingMap);

            Map<String, Object> bedMap = new HashMap<>();
            bedMap.put("min", publicity.getBedFeeMin());
            bedMap.put("max", publicity.getBedFeeMax());
            priceRanges.put("bed", bedMap);

            Map<String, Object> dietMap = new HashMap<>();
            dietMap.put("min", publicity.getMealFeeMin());
            dietMap.put("max", publicity.getMealFeeMax());
            priceRanges.put("diet", dietMap);

            // 计算总费用
            BigDecimal totalMin = publicity.getNursingFeeMin().add(publicity.getBedFeeMin()).add(publicity.getMealFeeMin());
            BigDecimal totalMax = publicity.getNursingFeeMax().add(publicity.getBedFeeMax()).add(publicity.getMealFeeMax());

            Map<String, Object> totalMap = new HashMap<>();
            totalMap.put("min", totalMin);
            totalMap.put("max", totalMax);
            priceRanges.put("total", totalMap);
            result.put("priceRanges", priceRanges);

            // **关键：设施图片需要转换为[{image, name}]格式**
            result.put("roomFacilities", parseFacilityImages(publicity.getRoomFacilities()));
            result.put("basicFacilities", parseFacilityImages(publicity.getBasicFacilities()));
            result.put("parkFacilities", parseFacilityImages(publicity.getParkFacilities()));

            // 其他设施数据 - 转换为前端期望格式
            result.put("lifeFacilities", parseLifeFacilities(publicity.getLifeFacilities()));
            result.put("medicalFacilities", parseMedicalFacilities(publicity.getMedicalFacilities()));
            result.put("dailyServices", parseDailyServices(publicity.getDailyServices()));

            // 图片数据
            result.put("coverImage", publicity.getMainPicture());
            result.put("images", parseJsonToArray(publicity.getEnvironmentImgs()));
            result.put("buildingArea", publicity.getBuildingArea());
        }

        // 添加图片分类数据
        // 优先从pension_institution_attach表获取，如果没有则从publicity表字段获取
        List<Map<String, Object>> groupedAttachments = pensionInstitutionAttachMapper.selectAttachmentsGroupByType(institution.getInstitutionId());
        List<InstitutionImageCategoryVO> imageCategories = new ArrayList<>();

        // 判断attach表是否有数据
        boolean hasAttachData = groupedAttachments != null && !groupedAttachments.isEmpty();

        if (hasAttachData) {
            // 从attach表获取
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_VR, "VR", "vr");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_MAIN, "主图", "main");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_ENVIRONMENT, "环境", "environment");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_ROOM, "房间", "room");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_BASIC, "设施", "basic");
            addImageCategory(imageCategories, groupedAttachments, PensionInstitutionAttach.TYPE_PARK, "园址", "park");
        } else if (publicity != null) {
            // 从publicity表字段获取（兼容管理端上传的图片）
            imageCategories.add(new InstitutionImageCategoryVO("vr", "VR", parseCommaSeparatedImages(publicity.getVrImage())));
            imageCategories.add(new InstitutionImageCategoryVO("main", "主图", parseCommaSeparatedImages(publicity.getMainPicture())));
            imageCategories.add(new InstitutionImageCategoryVO("environment", "环境", parseCommaSeparatedImages(publicity.getEnvironmentImgs())));
            imageCategories.add(new InstitutionImageCategoryVO("room", "房间", parseCommaSeparatedImages(publicity.getRoomFacilities())));
            imageCategories.add(new InstitutionImageCategoryVO("basic", "设施", parseCommaSeparatedImages(publicity.getBasicFacilities())));
            imageCategories.add(new InstitutionImageCategoryVO("park", "园址", parseCommaSeparatedImages(publicity.getParkFacilities())));
        } else {
            // 没有任何图片数据
            imageCategories.add(new InstitutionImageCategoryVO("vr", "VR", new ArrayList<>()));
            imageCategories.add(new InstitutionImageCategoryVO("main", "主图", new ArrayList<>()));
            imageCategories.add(new InstitutionImageCategoryVO("environment", "环境", new ArrayList<>()));
            imageCategories.add(new InstitutionImageCategoryVO("room", "房间", new ArrayList<>()));
            imageCategories.add(new InstitutionImageCategoryVO("basic", "设施", new ArrayList<>()));
            imageCategories.add(new InstitutionImageCategoryVO("park", "园址", new ArrayList<>()));
        }
        result.put("imageCategories", imageCategories);

        // 机构基础信息（用于详情页卡片展示）
        result.put("registeredCapital", institution.getRegisteredCapital()); // 注册资金
        result.put("bedCount", institution.getBedCount()); // 床位数
        result.put("careLevels", institution.getCareLevels()); // 收住对象类型

        // 收住对象���型解析（从publicity表的accept_elder_type获取）
        if (publicity != null && publicity.getAcceptElderType() != null && !publicity.getAcceptElderType().trim().isEmpty()) {
            result.put("acceptElderType", parseAcceptElderType(publicity.getAcceptElderType()));
        } else {
            result.put("acceptElderType", "");
        }

        // 成立时间格式化
        if (institution.getEstablishedDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            result.put("establishDate", sdf.format(institution.getEstablishedDate()));
        } else {
            result.put("establishDate", "");
        }

        // 默认值
        result.put("rating", 4.5);

        // **通过床位管理系统精确计算总床位和可用床位，确保与列表页面数据一致**
        try {
            Map<String, Object> bedStats = bedInfoService.getBedStatistics(institution.getInstitutionId());
            result.put("totalBeds", bedStats.getOrDefault("totalBeds", 0));
            result.put("availableBeds", bedStats.getOrDefault("availableBeds", 0));
        } catch (Exception e) {
            // 异常时使用机构表的床位数作为备用
            result.put("totalBeds", institution.getBedCount() != null ? institution.getBedCount() : 0);
            result.put("availableBeds", 0);
        }

        result.put("certificationTags", generateCertificationTags(institution.getInstitutionType()));
        result.put("reviews", new ArrayList<>()); // 暂时为空，后续更新

        // 添加机构评级数据
        try {
            InstitutionRating queryRating = new InstitutionRating();
            queryRating.setInstitutionId(institution.getInstitutionId());
            List<InstitutionRating> ratings = ratingService.selectInstitutionRatingList(queryRating);

            if (!ratings.isEmpty()) {
                // 获取最新的有效评级
                InstitutionRating latestRating = ratings.stream()
                        .filter(rating -> "1".equals(rating.getRatingStatus())) // 只获取有效的评级
                        .sorted((a, b) -> b.getRatingDate().compareTo(a.getRatingDate())) // 按评级日期倒序
                        .findFirst()
                        .orElse(null);

                if (latestRating != null) {
                    Map<String, Object> ratingData = new HashMap<>();
                    ratingData.put("ratingLevel", latestRating.getRatingLevel());
                    ratingData.put("totalScore", latestRating.getTotalScore());
                    ratingData.put("serviceScore", latestRating.getServiceScore());
                    ratingData.put("facilityScore", latestRating.getFacilityScore());
                    ratingData.put("managementScore", latestRating.getManagementScore());
                    ratingData.put("safetyScore", latestRating.getSafetyScore());
                    ratingData.put("ratingDate", latestRating.getRatingDate());
                    ratingData.put("expireDate", latestRating.getExpireDate());
                    ratingData.put("ratingStatus", latestRating.getRatingStatus());
                    ratingData.put("ratingRemark", latestRating.getRatingRemark());

                    result.put("institutionRating", ratingData);

                    // 更新评分为真实评级数据
                    result.put("rating", latestRating.getRatingLevel().doubleValue());
                } else {
                    // 没有有效评级，使用默认值
                    result.put("institutionRating", null);
                    result.put("rating", 4.5);
                }
            } else {
                // 没有评级数据
                result.put("institutionRating", null);
                result.put("rating", 4.5);
            }
        } catch (Exception e) {
            // 查询评级失败时使用默认值
            result.put("institutionRating", null);
            result.put("rating", 4.5);
            System.out.println("查询机构评级失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 解析设施图片，转换为前端需要的[{image, name}]格式
     * 支持两种格式：JSON格式和逗号分隔格式
     */
    private List<Map<String, String>> parseFacilityImages(String imageStr) {
        List<Map<String, String>> result = new ArrayList<>();
        if (!StringUtils.hasText(imageStr)) return result;

        try {
            // 尝试解析为JSON格式
            JSONArray jsonArray = JSON.parseArray(imageStr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Map<String, String> facility = new HashMap<>();
                facility.put("image", item.getString("url"));
                facility.put("name", item.getString("name"));
                result.add(facility);
            }
        } catch (Exception e) {
            // JSON解析失败，尝试解析为逗号分隔格式
            try {
                String[] imageUrls = imageStr.split(",");
                for (int i = 0; i < imageUrls.length; i++) {
                    String imageUrl = imageUrls[i].trim();
                    if (StringUtils.hasText(imageUrl)) {
                        Map<String, String> facility = new HashMap<>();
                        facility.put("image", imageUrl);
                        facility.put("name", "设施" + (i + 1)); // 生成默认名称
                        result.add(facility);
                    }
                }
            } catch (Exception ex) {
                // 解析完全失败，返回空列表
                System.out.println("解析设施图片失败: " + imageStr);
            }
        }
        return result;
    }

    /**
     * 生成认证标签（基于机构入驻申请的类型选项）
     */
    private List<String> generateCertificationTags(String institutionType) {
        List<String> tags = new ArrayList<>();

        // 基础标签
        tags.add("民政认证");
        tags.add("安全监管");

        // 基于机构入驻申请页面的类型选项生成标签
        switch (institutionType) {
            case "nursing_home":
                tags.add("养老院");
                tags.add("医养结合");
                break;
            case "service_center":
                tags.add("养老服务中心");
                tags.add("社区服务");
                break;
            case "day_care":
                tags.add("日间照料");
                tags.add("临时托管");
                break;
            case "senior_apartment":
                tags.add("养老公寓");
                tags.add("高端养老");
                break;
            case "other":
                tags.add("专业养老机构");
                break;
            default:
                tags.add("养老机构");
                break;
        }

        return tags;
    }

    private List<String> parseJsonToArray(String jsonStr) {
        if (!StringUtils.hasText(jsonStr)) return new ArrayList<>();
        try {
            return JSON.parseArray(jsonStr, String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 解析逗号分隔的图片字符串为列表
     */
    private List<String> parseCommaSeparatedImages(String imagesStr) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.hasText(imagesStr)) return result;

        String[] imageArray = imagesStr.split(",");
        for (String image : imageArray) {
            if (image != null && !image.trim().isEmpty()) {
                result.add(image.trim());
            }
        }
        return result;
    }

    /**
     * 解析每日服务安排，返回对象数组
     */
    private List<Map<String, Object>> parseDailyServices(String jsonStr) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!StringUtils.hasText(jsonStr)) return result;

        try {
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject service = jsonArray.getJSONObject(i);
                Map<String, Object> serviceMap = new HashMap<>();
                serviceMap.put("time", service.getString("time"));
                serviceMap.put("content", service.getString("content"));
                result.add(serviceMap);
            }
            return result;
        } catch (Exception e) {
            System.out.println("解析每日服务失败: " + jsonStr);
            return new ArrayList<>();
        }
    }

    /**
     * 解析生活设施，使用动态图标配置转换为前端需要的[{icon, name}]格式
     */
    private List<Map<String, String>> parseLifeFacilities(String jsonStr) {
        List<Map<String, String>> result = new ArrayList<>();
        if (!StringUtils.hasText(jsonStr)) return result;

        try {
            // 获取生活设施图标配置
            List<FacilityIconConfig> iconConfigs = facilityIconConfigService.selectByType("life");
            Map<String, String> iconMap = iconConfigs.stream()
                .collect(Collectors.toMap(FacilityIconConfig::getFacilityName, FacilityIconConfig::getIconName));

            // 解析设施数据
            List<String> facilities = JSON.parseArray(jsonStr, String.class);
            for (String facility : facilities) {
                Map<String, String> item = new HashMap<>();
                item.put("name", facility);
                item.put("icon", iconMap.getOrDefault(facility, "home")); // 默认使用home图标
                result.add(item);
            }
        } catch (Exception e) {
            System.out.println("解析生活设施失败: " + jsonStr);
        }
        return result;
    }

    /**
     * 解析医疗设施，使用动态图标配置转换为前端需要的[{icon, name}]格式
     */
    private List<Map<String, String>> parseMedicalFacilities(String jsonStr) {
        List<Map<String, String>> result = new ArrayList<>();
        if (!StringUtils.hasText(jsonStr)) return result;

        try {
            // 获取医疗设施图标配置
            List<FacilityIconConfig> iconConfigs = facilityIconConfigService.selectByType("medical");
            Map<String, String> iconMap = iconConfigs.stream()
                .collect(Collectors.toMap(FacilityIconConfig::getFacilityName, FacilityIconConfig::getIconName));

            // 解析设施数据
            List<String> facilities = JSON.parseArray(jsonStr, String.class);
            for (String facility : facilities) {
                Map<String, String> item = new HashMap<>();
                item.put("name", facility);
                item.put("icon", iconMap.getOrDefault(facility, "monitor")); // 默认使用monitor图标
                result.add(item);
            }
        } catch (Exception e) {
            System.out.println("解析医疗设施失败: " + jsonStr);
        }
        return result;
    }

    // 硬编码图标方法已被动态配置替代

    /**
     * 解析收住对象类型，转换为中文
     * 支持两种格式:
     * 1. 旧格式(英文代码): self_care,semi_disabled,disabled,dementia
     * 2. 新格式(数字代码): 1,2,3,4,5 (对应: 自理,半护理,全护理,失能,失智)
     */
    private String parseAcceptElderType(String acceptElderType) {
        if (!StringUtils.hasText(acceptElderType)) {
            return "";
        }

        String[] types = acceptElderType.split(",");
        StringBuilder result = new StringBuilder();

        // 判断是数字格式还是英文格式
        boolean isNumericFormat = types.length > 0 && types[0].trim().matches("\\d+");

        for (String type : types) {
            String trimmedType = type.trim();
            String chinese = "";

            if (isNumericFormat) {
                // 数字格式: 1=自理, 2=半护理, 3=全护理, 4=失能, 5=失智
                switch (trimmedType) {
                    case "1":
                        chinese = "自理";
                        break;
                    case "2":
                        chinese = "半护理";
                        break;
                    case "3":
                        chinese = "全护理";
                        break;
                    case "4":
                        chinese = "失能";
                        break;
                    case "5":
                        chinese = "失智";
                        break;
                    default:
                        chinese = trimmedType;
                        break;
                }
            } else {
                // 英文格式
                switch (trimmedType) {
                    case "self_care":
                        chinese = "自理";
                        break;
                    case "semi_disabled":
                        chinese = "半护理";
                        break;
                    case "disabled":
                        chinese = "失能";
                        break;
                    case "dementia":
                        chinese = "失智";
                        break;
                    default:
                        chinese = trimmedType;
                        break;
                }
            }

            if (result.length() > 0 && !chinese.isEmpty()) {
                result.append("、");
            }
            result.append(chinese);
        }

        return result.toString();
    }
}
