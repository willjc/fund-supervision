package com.ruoyi.web.controller.h5;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.BedInfo;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.PensionCheckinDTO;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.service.IOrderInfoService;
import com.ruoyi.service.IPensionCheckinService;

/**
 * H5订单Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5")
public class H5OrderController extends BaseController
{
    @Autowired
    private IBedInfoService bedInfoService;

    @Autowired
    private IElderInfoService elderInfoService;

    @Autowired
    private IPensionCheckinService pensionCheckinService;

    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 获取老人列表
     * 查询当前用户关联的老人信息
     */
    @GetMapping("/elder/list")
    public AjaxResult getElderList()
    {
        try {
            // 构建查询条件
            ElderInfo query = new ElderInfo();

            // 查询所有可用的老人（状态为未入住的）
            // TODO: 后续可以根据登录用户ID查询关联的老人
            List<ElderInfo> elderList = elderInfoService.selectElderInfoList(query);

            // 过滤掉已入住的老人（status != '1'）
            elderList = elderList.stream()
                .filter(elder -> !"1".equals(elder.getStatus()))
                .collect(java.util.stream.Collectors.toList());

            java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();

            for (ElderInfo elder : elderList) {
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("elderId", elder.getElderId());
                item.put("elderName", elder.getElderName());
                item.put("gender", elder.getGender());
                item.put("genderText", "1".equals(elder.getGender()) ? "男" : "女");
                item.put("age", elder.getAge());
                item.put("careLevel", elder.getCareLevel());
                item.put("careLevelText", getCareLevelText(elder.getCareLevel()));
                item.put("healthStatus", elder.getHealthStatus());
                item.put("phone", elder.getPhone());
                result.add(item);
            }

            return success(result);
        } catch (Exception e) {
            logger.error("获取老人列表失败", e);
            return error("获取老人列表失败：" + e.getMessage());
        }
    }

    /**
     * 将护理等级代码转换为文字
     */
    private String getCareLevelText(String careLevel) {
        switch (careLevel) {
            case "1":
                return "自理";
            case "2":
                return "半护理";
            case "3":
                return "全护理";
            default:
                return "自理";
        }
    }

    /**
     * 查询最优价格床位
     * 根据床位类型查询该类型下价格最低的可用床位
     */
    @GetMapping("/bed/optimal-price")
    public AjaxResult getOptimalPrice(@RequestParam Long institutionId,
                                     @RequestParam String bedType)
    {
        // 参数验证
        if (institutionId == null) {
            return error("机构ID不能为空");
        }
        if (!StringUtils.hasText(bedType)) {
            return error("床位类型不能为空");
        }

        // 构建查询条件
        BedInfo query = new BedInfo();
        query.setInstitutionId(institutionId);
        query.setBedType(bedType);
        query.setBedStatus("0"); // 0-空置

        // 查询可用床位
        List<BedInfo> availableBeds = bedInfoService.selectBedInfoList(query);

        if (availableBeds == null || availableBeds.isEmpty()) {
            return error("抱歉，该类型床位暂时已分配完毕。请选择其他房间类型或稍后再试。");
        }

        // 找出价格最低的床位
        BedInfo optimalBed = availableBeds.stream()
            .min((b1, b2) -> b1.getPrice().compareTo(b2.getPrice()))
            .orElse(null);

        if (optimalBed == null) {
            return error("抱歉，暂时没有找到合适的床位。请联系机构客服咨询床位情况。");
        }

        // 构建返回数据，包含护理等级价格信息
        Map<String, Object> result = new HashMap<>();
        result.put("bedId", optimalBed.getBedId());
        result.put("roomNumber", optimalBed.getRoomNumber());
        result.put("bedNumber", optimalBed.getBedNumber());
        result.put("bedType", optimalBed.getBedType());
        result.put("bedTypeName", getBedTypeName(optimalBed.getBedType()));
        result.put("bedFee", optimalBed.getPrice());
        result.put("selfCarePrice", optimalBed.getSelfCarePrice() != null ? optimalBed.getSelfCarePrice() : new BigDecimal("500"));
        result.put("halfCarePrice", optimalBed.getHalfCarePrice() != null ? optimalBed.getHalfCarePrice() : new BigDecimal("800"));
        result.put("fullCarePrice", optimalBed.getFullCarePrice() != null ? optimalBed.getFullCarePrice() : new BigDecimal("1200"));
        result.put("memberFee", optimalBed.getMemberFee() != null ? optimalBed.getMemberFee() : new BigDecimal("5000"));
        result.put("depositFee", optimalBed.getDepositFee() != null ? optimalBed.getDepositFee() : new BigDecimal("10000"));
        result.put("floorNumber", optimalBed.getFloorNumber());
        result.put("roomArea", optimalBed.getRoomArea());
        result.put("hasBathroom", optimalBed.getHasBathroom());
        result.put("hasBalcony", optimalBed.getHasBalcony());
        result.put("facilities", optimalBed.getFacilities());

        return success(result);
    }

    /**
     * 提交订单
     * 自动分配最便宜的可用床位并创建入住申请
     */
    @PostMapping("/order/submit")
    public AjaxResult submitOrder(@RequestBody Map<String, Object> orderData)
    {
        try {
            // 解析订单数据
            Long elderId = Long.valueOf(orderData.get("elderId").toString());
            Long institutionId = Long.valueOf(orderData.get("institutionId").toString());
            String bedType = orderData.get("roomType").toString(); // 直接使用床位类型代码
            String careLevel = orderData.get("careLevel").toString(); // 自理/半护理/全护理
            Integer monthCount = Integer.valueOf(orderData.get("monthCount").toString());

            // 验证老人信息
            ElderInfo elder = elderInfoService.selectElderInfoByElderId(elderId);
            if (elder == null) {
                return error("抱歉，未找到该老人信息。请重新选择或联系客服。");
            }

            // 查找最优价格床位
            BedInfo query = new BedInfo();
            query.setInstitutionId(institutionId);
            query.setBedType(bedType);
            query.setBedStatus("0"); // 0-空置
            List<BedInfo> availableBeds = bedInfoService.selectBedInfoList(query);

            if (availableBeds == null || availableBeds.isEmpty()) {
                return error("抱歉，该类型床位暂时已分配完毕。请选择其他房间类型或稍后再试。");
            }

            BedInfo selectedBed = availableBeds.stream()
                .min((b1, b2) -> b1.getPrice().compareTo(b2.getPrice()))
                .orElse(null);

            if (selectedBed == null) {
                return error("抱歉，暂时没有找到合适的床位。请联系机构客服咨询床位情况。");
            }

            // 创建入住申请DTO
            PensionCheckinDTO checkinDTO = new PensionCheckinDTO();

            // 老人信息
            checkinDTO.setElderName(elder.getElderName());
            checkinDTO.setGender(elder.getGender());
            checkinDTO.setIdCard(elder.getIdCard());
            checkinDTO.setBirthDate(elder.getBirthDate());
            checkinDTO.setAge(elder.getAge().intValue());
            checkinDTO.setPhone(elder.getPhone());
            checkinDTO.setAddress(elder.getAddress());
            checkinDTO.setCareLevel(careLevel);
            checkinDTO.setHealthStatus(elder.getHealthStatus());
            checkinDTO.setEmergencyContact(elder.getEmergencyContact());
            checkinDTO.setEmergencyPhone(elder.getEmergencyPhone());

            // 床位信息
            checkinDTO.setBedId(selectedBed.getBedId());
            checkinDTO.setCheckInDate(new Date()); // 默认今天入住

            // 费用计算（根据新的定价模式：服务费 = 床位费 + 护理费）
            BigDecimal bedFee = selectedBed.getPrice(); // 床位费
            BigDecimal careFee = getCareFeeByLevel(selectedBed, careLevel); // 护理费
            BigDecimal monthlyFee = bedFee.add(careFee); // 月服务费 = 床位费 + 护理费
            BigDecimal depositAmount = selectedBed.getDepositFee() != null ? selectedBed.getDepositFee() : new BigDecimal("10000");
            BigDecimal memberFee = selectedBed.getMemberFee() != null ? selectedBed.getMemberFee() : new BigDecimal("5000");
            BigDecimal finalAmount = monthlyFee.multiply(new BigDecimal(monthCount))
                                          .add(depositAmount)
                                          .add(memberFee);

            checkinDTO.setMonthlyFee(monthlyFee);
            checkinDTO.setMonthCount(monthCount);
            checkinDTO.setDepositAmount(depositAmount);
            checkinDTO.setMemberFee(memberFee);
            checkinDTO.setFinalAmount(finalAmount);
            checkinDTO.setFeeDescription(buildFeeDescription(bedFee, careFee, monthlyFee, depositAmount, memberFee, monthCount));

            // 支付方式（默认稍后支付）
            checkinDTO.setPaymentMethod("later");
            checkinDTO.setRemark("H5小程序订单来源");

            // 创建入住申请
            Long userId = null; // H5端可能没有登录用户，使用null
            int result = pensionCheckinService.createCheckin(checkinDTO, userId);

            if (result > 0) {
                // 查询刚创建的订单信息
                OrderInfo orderQuery = new OrderInfo();
                orderQuery.setElderId(elderId);
                orderQuery.setInstitutionId(institutionId);
                orderQuery.setOrderDate(new Date());
                List<OrderInfo> orderList = orderInfoService.selectOrderInfoList(orderQuery);

                OrderInfo latestOrder = null;
                if (orderList != null && !orderList.isEmpty()) {
                    // 找到最新创建的订单（按订单ID降序排列）
                    latestOrder = orderList.stream()
                        .max((o1, o2) -> o1.getOrderId().compareTo(o2.getOrderId()))
                        .orElse(null);
                }

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "订单提交成功");

                if (latestOrder != null) {
                    responseData.put("orderId", latestOrder.getOrderId());
                    responseData.put("orderNo", latestOrder.getOrderNo());
                } else {
                    // 如果查询不到订单信息，使用备用方案
                    responseData.put("orderId", System.currentTimeMillis());
                    responseData.put("orderNo", "H5" + System.currentTimeMillis());
                }

                responseData.put("bedInfo", selectedBed);
                responseData.put("monthlyFee", monthlyFee);
                responseData.put("totalAmount", finalAmount);
                responseData.put("checkInDate", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()));

                return success(responseData);
            } else {
                return error("订单提交失败");
            }

        } catch (Exception e) {
            logger.error("H5订单提交失败", e);
            return error("订单提交失败：" + e.getMessage());
        }
    }

    /**
     * 房间类型映射到床位类型
     * 单人间->2（豪华），双人间/三人间->1（普通），VIP->3（医疗）
     */
    private String mapRoomTypeToBedType(String roomType) {
        switch (roomType) {
            // 支持中文房间类型
            case "单人间":
                return "2"; // 豪华床位
            case "双人间":
            case "三人间":
                return "1"; // 普通床位
            case "VIP房间":
                return "3"; // 医疗床位
            // 兼容英文房间类型
            case "single":
                return "2"; // 豪华床位
            case "double":
            case "triple":
                return "1"; // 普通床位
            case "vip":
                return "3"; // 医疗床位
            default:
                return null;
        }
    }

    /**
     * 根据护理等级获取护理费
     * @param bed 床位信息
     * @param careLevel 护理等级（自理/半护理/全护理）
     * @return 护理费
     */
    private BigDecimal getCareFeeByLevel(BedInfo bed, String careLevel) {
        switch (careLevel) {
            case "自理":
                return bed.getSelfCarePrice() != null ? bed.getSelfCarePrice() : new BigDecimal("500");
            case "半护理":
                return bed.getHalfCarePrice() != null ? bed.getHalfCarePrice() : new BigDecimal("800");
            case "全护理":
                return bed.getFullCarePrice() != null ? bed.getFullCarePrice() : new BigDecimal("1200");
            default:
                return new BigDecimal("500"); // 默认自理价格
        }
    }

    /**
     * 获取床位类型名称
     */
    private String getBedTypeName(String bedType) {
        switch (bedType) {
            case "1":
                return "普通床位";
            case "2":
                return "豪华床位";
            case "3":
                return "医疗床位";
            default:
                return "未知类型";
        }
    }

    /**
     * 计算月费用
     * 床位费 + 餐费 + 护理费
     */
    private BigDecimal calculateMonthlyFee(BigDecimal bedPrice, String careLevel, String packageType) {
        BigDecimal mealFee = new BigDecimal("900"); // 餐费基础价
        BigDecimal careFee = new BigDecimal("0");   // 护理费

        // 根据护理等级计算护理费
        switch (careLevel) {
            case "1": // 自理
                careFee = new BigDecimal("300");
                break;
            case "2": // 半护理
                careFee = new BigDecimal("800");
                break;
            case "3": // 全护理
                careFee = new BigDecimal("1500");
                break;
        }

        // 根据套餐类型调整餐费
        switch (packageType) {
            case "basic":
                mealFee = mealFee.multiply(new BigDecimal("0.9"));
                break;
            case "standard":
                // 标准价格，不调整
                break;
            case "premium":
                mealFee = mealFee.multiply(new BigDecimal("1.2"));
                break;
        }

        return bedPrice.add(mealFee).add(careFee);
    }

    /**
     * 计算押金金额（通常是月费的2倍）
     */
    private BigDecimal calculateDepositAmount(BigDecimal monthlyFee) {
        return monthlyFee.multiply(new BigDecimal("2"));
    }

    /**
     * 计算会员费
     */
    private BigDecimal calculateMemberFee(String packageType) {
        switch (packageType) {
            case "basic":
                return new BigDecimal("0");
            case "standard":
                return new BigDecimal("200");
            case "premium":
                return new BigDecimal("500");
            default:
                return new BigDecimal("0");
        }
    }

    /**
     * 构建费用说明
     */
    private String buildFeeDescription(BigDecimal bedFee, BigDecimal careFee, BigDecimal monthlyFee,
                                     BigDecimal depositAmount, BigDecimal memberFee,
                                     Integer monthCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("床位费：").append(bedFee).append("元/月\n");
        sb.append("护理费：").append(careFee).append("元/月\n");
        sb.append("服务费合计：").append(monthlyFee).append("元/月\n");
        sb.append("缴费月数：").append(monthCount).append("个月\n");
        sb.append("押金：").append(depositAmount).append("元\n");
        if (memberFee.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("会员费：").append(memberFee).append("元\n");
        }
        BigDecimal total = monthlyFee.multiply(new BigDecimal(monthCount)).add(depositAmount).add(memberFee);
        sb.append("总计：").append(total).append("元");
        return sb.toString();
    }
}