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
import com.ruoyi.domain.BedInfo;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.PensionCheckinDTO;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.service.IElderInfoService;
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

  
    /**
     * 查询最优价格床位
     * 根据房间类型查询该类型下价格最低的可用床位
     */
    @GetMapping("/bed/optimal-price")
    public AjaxResult getOptimalPrice(@RequestParam Long institutionId,
                                     @RequestParam String roomType)
    {
        // 参数验证
        if (institutionId == null) {
            return error("机构ID不能为空");
        }
        if (!StringUtils.hasText(roomType)) {
            return error("房间类型不能为空");
        }

        // 房间类型映射到床位类型
        String bedType = mapRoomTypeToBedType(roomType);
        if (bedType == null) {
            return error("无效的房间类型");
        }

        // 构建查询条件
        BedInfo query = new BedInfo();
        query.setInstitutionId(institutionId);
        query.setBedType(bedType);
        query.setBedStatus("0"); // 0-空置

        // 查询可用床位
        List<BedInfo> availableBeds = bedInfoService.selectBedInfoList(query);

        if (availableBeds == null || availableBeds.isEmpty()) {
            return error("该类型下暂无可用床位");
        }

        // 找出价格最低的床位
        BedInfo optimalBed = availableBeds.stream()
            .min((b1, b2) -> b1.getPrice().compareTo(b2.getPrice()))
            .orElse(null);

        if (optimalBed == null) {
            return error("未找到合适的床位");
        }

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("bedId", optimalBed.getBedId());
        result.put("roomNumber", optimalBed.getRoomNumber());
        result.put("bedNumber", optimalBed.getBedNumber());
        result.put("bedType", optimalBed.getBedType());
        result.put("bedTypeName", getBedTypeName(optimalBed.getBedType()));
        result.put("price", optimalBed.getPrice());
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
            String roomType = orderData.get("roomType").toString();
            String careLevel = orderData.get("careLevel").toString();
            Integer monthCount = Integer.valueOf(orderData.get("monthCount").toString());
            String packageType = orderData.get("packageType") != null ? orderData.get("packageType").toString() : "standard";

            // 验证老人信息
            ElderInfo elder = elderInfoService.selectElderInfoByElderId(elderId);
            if (elder == null) {
                return error("老人信息不存在");
            }

            // 房间类型映射到床位类型
            String bedType = mapRoomTypeToBedType(roomType);
            if (bedType == null) {
                return error("无效的房间类型");
            }

            // 查找最优价格床位
            BedInfo query = new BedInfo();
            query.setInstitutionId(institutionId);
            query.setBedType(bedType);
            query.setBedStatus("0"); // 0-空置
            List<BedInfo> availableBeds = bedInfoService.selectBedInfoList(query);

            if (availableBeds == null || availableBeds.isEmpty()) {
                return error("该类型下暂无可用床位");
            }

            BedInfo selectedBed = availableBeds.stream()
                .min((b1, b2) -> b1.getPrice().compareTo(b2.getPrice()))
                .orElse(null);

            if (selectedBed == null) {
                return error("未找到合适的床位");
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

            // 费用计算
            BigDecimal bedPrice = selectedBed.getPrice();
            BigDecimal monthlyFee = calculateMonthlyFee(bedPrice, careLevel, packageType);
            BigDecimal depositAmount = calculateDepositAmount(monthlyFee);
            BigDecimal memberFee = calculateMemberFee(packageType);
            BigDecimal finalAmount = monthlyFee.multiply(new BigDecimal(monthCount))
                                          .add(depositAmount)
                                          .add(memberFee);

            checkinDTO.setMonthlyFee(monthlyFee);
            checkinDTO.setMonthCount(monthCount);
            checkinDTO.setDepositAmount(depositAmount);
            checkinDTO.setMemberFee(memberFee);
            checkinDTO.setFinalAmount(finalAmount);
            checkinDTO.setFeeDescription(buildFeeDescription(bedPrice, monthlyFee, depositAmount, memberFee, monthCount));

            // 支付方式（默认稍后支付）
            checkinDTO.setPaymentMethod("later");
            checkinDTO.setRemark("H5小程序订单来源");

            // 创建入住申请
            Long userId = null; // H5端可能没有登录用户，使用null
            int result = pensionCheckinService.createCheckin(checkinDTO, userId);

            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "订单提交成功");
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
    private String buildFeeDescription(BigDecimal bedPrice, BigDecimal monthlyFee,
                                     BigDecimal depositAmount, BigDecimal memberFee,
                                     Integer monthCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("床位费：").append(bedPrice).append("元/月\n");
        sb.append("月服务费：").append(monthlyFee).append("元/月\n");
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