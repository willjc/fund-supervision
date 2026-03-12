package com.ruoyi.web.controller.h5;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.domain.PensionCheckinDTO;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.service.IOrderInfoService;
import com.ruoyi.service.IOrderItemService;
import com.ruoyi.service.IPensionCheckinService;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.domain.pension.ExpenseRecord;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.service.pension.IDepositApplyService;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.IPaymentRecordService;
import com.ruoyi.domain.pension.MealFeeConfig;
import com.ruoyi.service.IMealFeeConfigService;
import com.ruoyi.service.IResidentService;
import com.ruoyi.domain.RenewDTO;
import com.ruoyi.domain.BedAllocation;
import com.ruoyi.domain.vo.ElderCurrentPriceVO;
import com.ruoyi.domain.pension.FundTransfer;

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

    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private IElderFamilyService elderFamilyService;

    @Autowired
    private IPensionInstitutionService institutionService;

    @Autowired
    private IPensionInstitutionPublicService institutionPublicService;

    @Autowired
    private IAccountInfoService accountInfoService;

    @Autowired
    private IDepositApplyService depositApplyService;

    @Autowired
    private IExpenseRecordService expenseRecordService;

    @Autowired
    private IPaymentRecordService paymentRecordService;

    @Autowired
    private IMealFeeConfigService mealFeeConfigService;

    @Autowired
    private com.ruoyi.service.pension.ISupervisionAccountLogService supervisionAccountLogService;

    @Autowired
    private com.ruoyi.service.pension.IFundTransferService fundTransferService;

    @Autowired
    private com.ruoyi.mapper.BedAllocationMapper bedAllocationMapper;

    @Autowired
    private IResidentService residentService;

    /**
     * 获取订单列表
     * 根据当前登录用户关联的老人查询订单（家属或老人本人）
     */
    @GetMapping("/order/list")
    public AjaxResult getOrderList(@RequestParam(required = false) Long elderId,
                                   @RequestParam(required = false) String orderStatus,
                                   @RequestParam(required = false) String searchValue,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize)
    {
        try {
            // 获取当前用户身份
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 获取当前用户关联的老人ID列表（通过elder_family表）
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                // 用户没有关联任何老人，返回空结果
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("rows", new java.util.ArrayList<>());
                result.put("total", 0);
                result.put("pageNum", pageNum);
                result.put("pageSize", pageSize);
                return success(result);
            }

            // 构建用户可访问的老人ID列表
            List<Long> accessibleElderIds = new java.util.ArrayList<>();
            for (ElderFamily family : familyList) {
                if (family.getElderId() != null && "0".equals(family.getStatus())) {
                    accessibleElderIds.add(family.getElderId());
                }
            }

            // 如果前端传入了elderId参数，验证是否有权访问该老人
            if (elderId != null && elderId > 0) {
                if (!accessibleElderIds.contains(elderId)) {
                    return error("无权访问该老人的订单");
                }
                // 只查询指定老人的订单
                accessibleElderIds.clear();
                accessibleElderIds.add(elderId);
            }

            // 查询这些老人的所有订单
            List<OrderInfo> orderList = new java.util.ArrayList<>();
            for (Long eid : accessibleElderIds) {
                OrderInfo query = new OrderInfo();
                query.setElderId(eid);
                List<OrderInfo> elderOrders = orderInfoService.selectOrderInfoList(query);
                if (elderOrders != null) {
                    orderList.addAll(elderOrders);
                }
            }

            // 如果有搜索关键词，按订单号或机构名称过滤
            if (StringUtils.hasText(searchValue)) {
                List<OrderInfo> filteredOrders = new java.util.ArrayList<>();
                String keyword = searchValue.trim().toLowerCase();

                for (OrderInfo order : orderList) {
                    boolean match = false;

                    // 匹配订单号
                    if (order.getOrderNo() != null && order.getOrderNo().toLowerCase().contains(keyword)) {
                        match = true;
                    }

                    // 匹配机构名称
                    if (!match && order.getInstitutionId() != null) {
                        PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(order.getInstitutionId());
                        if (institution != null && institution.getInstitutionName() != null
                                && institution.getInstitutionName().toLowerCase().contains(keyword)) {
                            match = true;
                        }
                    }

                    if (match) {
                        filteredOrders.add(order);
                    }
                }

                orderList = filteredOrders;
            }

            // 如果提供了订单状态，过滤订单
            if ("pending".equals(orderStatus)) {
                // pending 表示待付款，包括状态 '0'（续费订单待支付）和 '5'（入驻订单审核通过待付款）
                List<OrderInfo> pendingOrders = new java.util.ArrayList<>();
                for (OrderInfo order : orderList) {
                    String status = order.getOrderStatus();
                    if ("0".equals(status) || "5".equals(status)) {
                        pendingOrders.add(order);
                    }
                }
                orderList = pendingOrders;
            } else if (orderStatus != null && !orderStatus.isEmpty() && !"all".equals(orderStatus)) {
                List<OrderInfo> filteredOrders = new java.util.ArrayList<>();
                for (OrderInfo order : orderList) {
                    if (orderStatus.equals(order.getOrderStatus())) {
                        filteredOrders.add(order);
                    }
                }
                orderList = filteredOrders;
            }

            // 分页处理
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = startIndex + pageSize;

            List<OrderInfo> paginatedList = new java.util.ArrayList<>();
            if (orderList != null) {
                // 按创建时间倒序排序
                orderList.sort((o1, o2) -> {
                    if (o2.getCreateTime() == null || o1.getCreateTime() == null) {
                        return 0;
                    }
                    return o2.getCreateTime().compareTo(o1.getCreateTime());
                });

                // 分页截取
                for (int i = startIndex; i < endIndex && i < orderList.size(); i++) {
                    paginatedList.add(orderList.get(i));
                }
            }

            // 构建返回数据
            Map<String, Object> result = new java.util.HashMap<>();
            java.util.List<Map<String, Object>> orderListData = new java.util.ArrayList<>();

            for (OrderInfo order : paginatedList) {
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("orderId", order.getOrderId());
                item.put("orderNo", order.getOrderNo());
                item.put("orderType", order.getOrderType());
                item.put("orderTypeText", getOrderTypeText(order.getOrderType()));
                item.put("orderStatus", order.getOrderStatus());
                item.put("orderStatusText", getOrderStatusText(order.getOrderStatus()));
                item.put("orderAmount", order.getOrderAmount());
                item.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, order.getCreateTime()));
                item.put("elderId", order.getElderId());

                // 获取机构信息
                if (order.getInstitutionId() != null) {
                    item.put("institutionId", order.getInstitutionId());
                    // 根据机构ID查询机构名称
                    PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(order.getInstitutionId());
                    if (institution != null) {
                        item.put("institutionName", institution.getInstitutionName());
                    }
                }

                orderListData.add(item);
            }

            result.put("rows", orderListData);
            result.put("total", orderList != null ? orderList.size() : 0);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            return success(result);
        } catch (Exception e) {
            logger.error("获取订单列表失败", e);
            return error("获取订单列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     * 根据订单ID查询订单详情，验证用户有权限访问
     */
    @GetMapping("/order/{orderId}")
    public AjaxResult getOrderDetail(@PathVariable Long orderId)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 查询订单信息
            OrderInfo order = orderInfoService.selectOrderInfoByOrderId(orderId);
            if (order == null) {
                return error("订单不存在");
            }

            // 验证用户是否有权限访问该订单
            Long orderCreatorId = order.getCreatorUserId();
            if (orderCreatorId == null || !orderCreatorId.equals(currentUserId)) {
                // 检查用户是否是该订单的老人的家属
                ElderFamily familyQuery = new ElderFamily();
                familyQuery.setUserId(currentUserId);
                familyQuery.setElderId(order.getElderId());
                List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

                if (familyList == null || familyList.isEmpty()) {
                    return error("无权访问该订单信息");
                }
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getOrderId());
            result.put("orderNo", order.getOrderNo());
            result.put("orderType", order.getOrderType());
            result.put("orderTypeText", getOrderTypeText(order.getOrderType()));
            result.put("orderStatus", order.getOrderStatus());
            result.put("orderStatusText", getOrderStatusText(order.getOrderStatus()));
            result.put("orderAmount", order.getOrderAmount());
            result.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, order.getCreateTime()));

            // 获取老人信息
            if (order.getElderId() != null) {
                ElderInfo elder = elderInfoService.selectElderInfoByElderId(order.getElderId());
                if (elder != null) {
                    result.put("elderName", elder.getElderName());
                    result.put("elderAge", elder.getAge());
                    result.put("elderGender", elder.getGender());
                }
            }

            // 获取机构信息
            if (order.getInstitutionId() != null) {
                PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(order.getInstitutionId());
                if (institution != null) {
                    result.put("institutionId", institution.getInstitutionId());
                    result.put("institutionName", institution.getInstitutionName());
                    result.put("institutionAddress", institution.getActualAddress());
                    result.put("institutionPhone", institution.getContactPhone());

                    // 获取机构公示信息中的主图
                    PensionInstitutionPublic publicInfo = institutionPublicService.selectPensionInstitutionPublicByInstitutionId(order.getInstitutionId());
                    if (publicInfo != null && publicInfo.getMainPicture() != null && !publicInfo.getMainPicture().isEmpty()) {
                        result.put("institutionCover", publicInfo.getMainPicture());
                    }
                }
            }

            // 获取床位信息 - 直接使用 order 对象中已有的数据（Mapper 已关联查询）
            if (order.getRoomNumber() != null && order.getBedNumber() != null) {
                result.put("bedInfo", order.getRoomNumber() + "-" + order.getBedNumber());
                result.put("roomNumber", order.getRoomNumber());
                result.put("bedNumber", order.getBedNumber());
                if (order.getBedId() != null) {
                    result.put("bedId", order.getBedId());
                }
            }

            // 构建费用明细 - 从床位和订单信息中提取详细费用
            java.util.List<Map<String, Object>> feeItems = new java.util.ArrayList<>();
            BigDecimal bedFee = BigDecimal.ZERO;
            BigDecimal careFee = BigDecimal.ZERO;
            BigDecimal depositAmount = BigDecimal.ZERO;
            BigDecimal memberFee = BigDecimal.ZERO;
            Integer monthCount = order.getMonthCount() != null ? order.getMonthCount() : 1;

            // 优先从remark字段解析所有费用（因为remark包含完整的费用信息）
            if (order.getRemark() != null) {
                try {
                    String remark = order.getRemark();
                    // 从remark中解析：床位费：XXXX.XXX元/月
                    if (remark.contains("床位费：")) {
                        String[] parts = remark.split("床位费：")[1].split("元/月");
                        bedFee = new BigDecimal(parts[0].trim());
                    }
                    // 从remark中解析：护理费：XXXX.XXX元/月
                    if (remark.contains("护理费：")) {
                        String[] parts = remark.split("护理费：")[1].split("元/月");
                        careFee = new BigDecimal(parts[0].trim());
                    }
                    // 从remark中解析：押金：XXXX.XXX元
                    if (remark.contains("押金：")) {
                        String[] parts = remark.split("押金：")[1].split("元");
                        depositAmount = new BigDecimal(parts[0].trim());
                    }
                    // 从remark中解析：会员费：XXXX.XXX元
                    if (remark.contains("会员费：")) {
                        String[] parts = remark.split("会员费：")[1].split("元");
                        memberFee = new BigDecimal(parts[0].trim());
                    }
                } catch (Exception e) {
                    logger.warn("解析remark字段失败", e);
                }
            }

            // 如果从remark解析失败，则从bed_info表查询
            if (bedFee.compareTo(BigDecimal.ZERO) == 0 && order.getBedId() != null) {
                BedInfo bed = bedInfoService.selectBedInfoByBedId(order.getBedId());
                if (bed != null) {
                    bedFee = bed.getPrice() != null ? bed.getPrice() : BigDecimal.ZERO;
                    depositAmount = bed.getDepositFee() != null ? bed.getDepositFee() : BigDecimal.ZERO;
                    memberFee = bed.getMemberFee() != null ? bed.getMemberFee() : BigDecimal.ZERO;
                }
            }

            // 构建费用项数组 - 按照确认订单页的顺序
            if (bedFee.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new java.util.HashMap<>();
                feeItem.put("name", "床位费");
                feeItem.put("amount", bedFee);
                feeItems.add(feeItem);
            }

            if (careFee.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new java.util.HashMap<>();
                feeItem.put("name", "护理费");
                feeItem.put("amount", careFee);
                feeItems.add(feeItem);
            }

            if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new java.util.HashMap<>();
                feeItem.put("name", "押金");
                feeItem.put("amount", depositAmount);
                feeItems.add(feeItem);
            }

            if (memberFee.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new java.util.HashMap<>();
                feeItem.put("name", "会员费");
                feeItem.put("amount", memberFee);
                feeItems.add(feeItem);
            }

            result.put("feeItems", feeItems);
            result.put("monthCount", monthCount);
            result.put("discountAmount", order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
            result.put("paidAmount", order.getPaidAmount() != null ? order.getPaidAmount() : order.getOrderAmount());

            return success(result);
        } catch (Exception e) {
            logger.error("获取订单详情失败", e);
            return error("获取订单详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单明细
     * 根据订单ID查询订单明细，包含价格变更信息
     */
    @GetMapping("/order/{orderId}/items")
    public AjaxResult getOrderItems(@PathVariable Long orderId)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 查询订单信息，验证权限
            OrderInfo order = orderInfoService.selectOrderInfoByOrderId(orderId);
            if (order == null) {
                return error("订单不存在");
            }

            // 验证用户是否有权限访问该订单
            Long orderCreatorId = order.getCreatorUserId();
            if (orderCreatorId == null || !orderCreatorId.equals(currentUserId)) {
                // 检查用户是否是该订单的老人的家属
                ElderFamily familyQuery = new ElderFamily();
                familyQuery.setUserId(currentUserId);
                familyQuery.setElderId(order.getElderId());
                List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

                if (familyList == null || familyList.isEmpty()) {
                    return error("无权访问该订单信息");
                }
            }

            // 查询订单明细
            List<OrderItem> items = orderItemService.selectOrderItemsByOrderId(orderId);

            return success(items);
        } catch (Exception e) {
            logger.error("获取订单明细失败", e);
            return error("获取订单明细失败：" + e.getMessage());
        }
    }

    /**
     * 通过订单号获取订单详情
     * 根据订单编号查询订单详情，用于支付成功页面跳转
     */
    @GetMapping("/getOrderByNo")
    public AjaxResult getOrderByNo(@RequestParam String orderNo)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 验证订单号参数
            if (!StringUtils.hasText(orderNo)) {
                return error("订单编号不能为空");
            }

            // 查询订单信息
            OrderInfo query = new OrderInfo();
            query.setOrderNo(orderNo);
            OrderInfo order = orderInfoService.selectOrderInfoList(query).stream()
                .findFirst()
                .orElse(null);

            if (order == null) {
                return error("订单不存在");
            }

            // 验证用户是否有权限访问该订单
            Long orderCreatorId = order.getCreatorUserId();
            if (orderCreatorId == null || !orderCreatorId.equals(currentUserId)) {
                // 检查用户是否是该订单的老人的家属
                ElderFamily familyQuery = new ElderFamily();
                familyQuery.setUserId(currentUserId);
                familyQuery.setElderId(order.getElderId());
                List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

                if (familyList == null || familyList.isEmpty()) {
                    return error("无权访问该订单信息");
                }
            }

            // 构建返回数据（与getOrderDetail相同）
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getOrderId());
            result.put("orderNo", order.getOrderNo());
            result.put("orderType", order.getOrderType());
            result.put("orderTypeText", getOrderTypeText(order.getOrderType()));
            result.put("orderStatus", order.getOrderStatus());
            result.put("orderStatusText", getOrderStatusText(order.getOrderStatus()));
            result.put("orderAmount", order.getOrderAmount());
            result.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, order.getCreateTime()));

            // 获取老人信息
            if (order.getElderId() != null) {
                ElderInfo elder = elderInfoService.selectElderInfoByElderId(order.getElderId());
                if (elder != null) {
                    result.put("elderName", elder.getElderName());
                    result.put("elderAge", elder.getAge());
                    result.put("elderGender", elder.getGender());
                }
            }

            // 获取机构信息
            if (order.getInstitutionId() != null) {
                PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(order.getInstitutionId());
                if (institution != null) {
                    result.put("institutionId", institution.getInstitutionId());
                    result.put("institutionName", institution.getInstitutionName());
                    result.put("institutionAddress", institution.getActualAddress());
                    result.put("institutionPhone", institution.getContactPhone());

                    // 获取机构公示信息中的主图
                    PensionInstitutionPublic publicInfo = institutionPublicService.selectPensionInstitutionPublicByInstitutionId(order.getInstitutionId());
                    if (publicInfo != null && publicInfo.getMainPicture() != null && !publicInfo.getMainPicture().isEmpty()) {
                        result.put("institutionCover", publicInfo.getMainPicture());
                    }
                }
            }

            // 获取床位信息 - 直接使用 order 对象中已有的数据（Mapper 已关联查询）
            if (order.getRoomNumber() != null && order.getBedNumber() != null) {
                result.put("bedInfo", order.getRoomNumber() + "-" + order.getBedNumber());
                result.put("roomNumber", order.getRoomNumber());
                result.put("bedNumber", order.getBedNumber());
                if (order.getBedId() != null) {
                    result.put("bedId", order.getBedId());
                }
            }

            // 构建费用明细 - 从床位和订单信息中提取详细费用
            java.util.List<Map<String, Object>> feeItems = new java.util.ArrayList<>();
            BigDecimal bedFee = BigDecimal.ZERO;
            BigDecimal careFee = BigDecimal.ZERO;
            BigDecimal depositAmount = BigDecimal.ZERO;
            BigDecimal memberFee = BigDecimal.ZERO;
            Integer monthCount = order.getMonthCount() != null ? order.getMonthCount() : 1;

            // 优先从remark字段解析所有费用（因为remark包含完整的费用信息）
            if (order.getRemark() != null) {
                try {
                    String remark = order.getRemark();
                    // 从remark中解析：床位费：XXXX.XXX元/月
                    if (remark.contains("床位费：")) {
                        String[] parts = remark.split("床位费：")[1].split("元/月");
                        bedFee = new BigDecimal(parts[0].trim());
                    }
                    // 从remark中解析：护理费：XXXX.XXX元/月
                    if (remark.contains("护理费：")) {
                        String[] parts = remark.split("护理费：")[1].split("元/月");
                        careFee = new BigDecimal(parts[0].trim());
                    }
                    // 从remark中解析：押金：XXXX.XXX元
                    if (remark.contains("押金：")) {
                        String[] parts = remark.split("押金：")[1].split("元");
                        depositAmount = new BigDecimal(parts[0].trim());
                    }
                    // 从remark中解析：会员费：XXXX.XXX元
                    if (remark.contains("会员费：")) {
                        String[] parts = remark.split("会员费：")[1].split("元");
                        memberFee = new BigDecimal(parts[0].trim());
                    }
                } catch (Exception e) {
                    logger.warn("解析remark字段失败", e);
                }
            }

            // 如果从remark解析失败，则从bed_info表查询
            if (bedFee.compareTo(BigDecimal.ZERO) == 0 && order.getBedId() != null) {
                BedInfo bed = bedInfoService.selectBedInfoByBedId(order.getBedId());
                if (bed != null) {
                    bedFee = bed.getPrice() != null ? bed.getPrice() : BigDecimal.ZERO;
                    depositAmount = bed.getDepositFee() != null ? bed.getDepositFee() : BigDecimal.ZERO;
                    memberFee = bed.getMemberFee() != null ? bed.getMemberFee() : BigDecimal.ZERO;
                }
            }

            // 构建费用项数组 - 按照确认订单页的顺序
            if (bedFee.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new HashMap<>();
                feeItem.put("name", "床位费");
                feeItem.put("amount", bedFee);
                feeItems.add(feeItem);
            }

            if (careFee.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new HashMap<>();
                feeItem.put("name", "护理费");
                feeItem.put("amount", careFee);
                feeItems.add(feeItem);
            }

            if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new HashMap<>();
                feeItem.put("name", "押金");
                feeItem.put("amount", depositAmount);
                feeItems.add(feeItem);
            }

            if (memberFee.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new HashMap<>();
                feeItem.put("name", "会员费");
                feeItem.put("amount", memberFee);
                feeItems.add(feeItem);
            }

            result.put("feeItems", feeItems);
            result.put("monthCount", monthCount);
            result.put("discountAmount", order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
            result.put("paidAmount", order.getPaidAmount() != null ? order.getPaidAmount() : order.getOrderAmount());

            return success(result);
        } catch (Exception e) {
            logger.error("通过订单号获取订单详情失败", e);
            return error("通过订单号获取订单详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取老人列表
     * 查询当前用户关联的老人信息（只返回该用户添加的老人）
     */
    @GetMapping("/elder/list")
    public AjaxResult getElderList()
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 查询当前用户关联的所有老人ID
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            // 如果用户没有关联任何老人，返回空结果
            if (familyList == null || familyList.isEmpty()) {
                return success(new java.util.ArrayList<>());
            }

            // 构建用户可访问的老人ID列表
            List<Long> accessibleElderIds = new java.util.ArrayList<>();
            for (ElderFamily family : familyList) {
                if (family.getElderId() != null) {
                    accessibleElderIds.add(family.getElderId());
                }
            }

            // 查询这些老人的详细信息
            List<ElderInfo> elderList = new java.util.ArrayList<>();
            for (Long elderId : accessibleElderIds) {
                ElderInfo elder = elderInfoService.selectElderInfoByElderId(elderId);
                if (elder != null && !"2".equals(elder.getStatus())) {
                    elderList.add(elder);
                }
            }

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
     * 获取老人账户余额信息
     *
     * 计算公式（用户端H5）：
     * - 账户余额 = 未拨付拨付单金额 + 会员费 + 押金
     * - 押金 = 老人押金 - 已拨付押金金额（即账户表deposit_balance当前值）
     * - 预存 = 未拨付拨付单金额
     */
    @GetMapping("/account/{elderId}")
    public AjaxResult getAccountInfo(@PathVariable Long elderId) {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 验证用户是否有权限访问该老人信息
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            familyQuery.setElderId(elderId);
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                return error("无权访问该老人信息");
            }

            // 查询账户信息
            AccountInfo account = accountInfoService.selectAccountInfoByElderId(elderId);
            if (account == null) {
                // 返回默认值而不是错误，支持没有账户的老人
                Map<String, Object> defaultAccount = new HashMap<>();
                defaultAccount.put("accountId", null);
                defaultAccount.put("accountNo", null);
                BigDecimal zeroBalance = new BigDecimal("0");
                defaultAccount.put("totalBalance", zeroBalance);
                defaultAccount.put("serviceBalance", zeroBalance);
                defaultAccount.put("depositBalance", zeroBalance);
                defaultAccount.put("memberBalance", zeroBalance);
                defaultAccount.put("prepaidAmount", zeroBalance);
                defaultAccount.put("hasAccount", false);
                return success(defaultAccount);
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("accountId", account.getAccountId());
            result.put("accountNo", account.getAccountNo());

            // 获取账户表余额
            BigDecimal serviceBalance = account.getServiceBalance() != null ? account.getServiceBalance() : new BigDecimal("0");
            BigDecimal depositBalance = account.getDepositBalance() != null ? account.getDepositBalance() : new BigDecimal("0");
            BigDecimal memberBalance = account.getMemberBalance() != null ? account.getMemberBalance() : new BigDecimal("0");

            // 计算已划拨的服务费金额（paid_method IN ('auto', 'manual') 且 status='completed' 且 is_paid='1'）
            BigDecimal transferredServiceAmount = BigDecimal.ZERO;
            List<com.ruoyi.domain.pension.FundTransfer> serviceTransfers =
                fundTransferService.selectByElderIdAndPaidMethods(elderId, new String[]{"auto", "manual"});
            for (com.ruoyi.domain.pension.FundTransfer ft : serviceTransfers) {
                if ("completed".equals(ft.getStatus()) && "1".equals(ft.getIsPaid()) && ft.getTransferAmount() != null) {
                    transferredServiceAmount = transferredServiceAmount.add(ft.getTransferAmount());
                }
            }

            // 未拨付拨付单金额 = 服务费余额 - 已划拨服务费
            BigDecimal unpaidTransferAmount = serviceBalance.subtract(transferredServiceAmount);
            if (unpaidTransferAmount.compareTo(BigDecimal.ZERO) < 0) {
                unpaidTransferAmount = BigDecimal.ZERO;
            }

            // 押金显示值 = 老人押金 - 已拨付押金金额
            // 注意：账户表的deposit_balance在押金审批通过时已经被扣除，所以直接使用即可
            result.put("depositBalance", depositBalance);

            // 预存 = 未拨付拨付单金额
            result.put("prepaidAmount", unpaidTransferAmount);

            // 账户余额 = 未拨付拨付单金额 + 会员费 + 押金
            BigDecimal totalBalance = unpaidTransferAmount.add(memberBalance).add(depositBalance);
            result.put("totalBalance", totalBalance);

            result.put("serviceBalance", serviceBalance);  // 原始服务费余额（仅供参考）
            result.put("memberBalance", memberBalance);    // 会员费余额
            result.put("hasAccount", true);

            // 添加机构信息
            Long institutionId = account.getInstitutionId();
            if (institutionId != null) {
                result.put("institutionId", institutionId);
                PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(institutionId);
                result.put("institutionName", institution != null ? institution.getInstitutionName() : "");
            }

            return success(result);
        } catch (Exception e) {
            logger.error("获取账户信息失败", e);
            return error("获取账户信息失败：" + e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/order/{orderId}/cancel")
    public AjaxResult cancelOrder(@PathVariable Long orderId) {
        try {
            logger.info("开始处理取消订单请求，订单ID：{}", orderId);

            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                logger.error("取消订单失败：用户未登录或身份验证失败，订单ID：{}", orderId);
                return error("用户未登录或身份验证失败");
            }

            logger.info("当前用户ID：{}", currentUserId);

            // 查询订单信息
            OrderInfo order = orderInfoService.selectOrderInfoByOrderId(orderId);
            if (order == null) {
                logger.error("取消订单失败：订单不存在，订单ID：{}", orderId);
                return error("订单不存在");
            }

            logger.info("查询到订单信息：订单号={}，状态={}，创建者={}，金额={}",
                order.getOrderNo(), order.getOrderStatus(), order.getCreatorUserId(), order.getOrderAmount());

            // 验证订单权限 - 验证当前用户是否有权操作该订单关联的老人
            // 通过elder_family表验证用户是否为老人的家属或本人
            if (order.getElderId() != null && !hasElderAccess(currentUserId, order.getElderId())) {
                logger.error("取消订单失败：无权限操作该订单，订单ID={}，当前用户={}，老人ID={}",
                    orderId, currentUserId, order.getElderId());
                return error("无权限操作该订单");
            }

            // 验证订单状态是否可以取消
            String currentStatus = order.getOrderStatus();
            // 只有待审核(4)和待付款(5)的订单才能取消
            if (!"0".equals(currentStatus) && !"4".equals(currentStatus) && !"5".equals(currentStatus)) {
                logger.error("取消订单失败：订单状态不允许取消，订单ID={}，当前状态={}", orderId, currentStatus);
                return error("只有待审核或待付款的订单才能取消");
            }

            // 更新订单状态为已取消
            order.setOrderStatus("2"); // 2-已取消
            order.setUpdateTime(new Date());

            logger.info("开始更新订单状态，订单ID={}，新状态=2（已取消）", orderId);

            int result = orderInfoService.updateOrderInfo(order);
            if (result > 0) {
                // 释放床位��源
                if (order.getBedId() != null) {
                    try {
                        // 1. 删除床位分配记录
                        bedAllocationMapper.deleteBedAllocationByBedId(order.getBedId());
                        logger.info("删除床位分配记录成功，床位ID：{}", order.getBedId());

                        // 2. 更新床位状态为空置
                        bedInfoService.updateBedStatus(order.getBedId(), "0");
                        logger.info("床位状态已更新为空置，床位ID：{}", order.getBedId());
                    } catch (Exception e) {
                        logger.error("释放床位资源失败，床位ID：{}", order.getBedId(), e);
                        // 床位释放失败不影响订单取消流程
                    }
                }

                // 恢复老人状态（仅针对入驻订单）
                // 续费订单取消不影响老人入住状态
                if (order.getElderId() != null) {
                    try {
                        ElderInfo elder = elderInfoService.selectElderInfoByElderId(order.getElderId());
                        if (elder != null) {
                            // 只有入驻订单（order_type="1"）取消时才恢复为待入住
                            // 续费订单（order_type="2"）取消不影响老人入住状态
                            if ("1".equals(order.getOrderType())) {
                                elder.setStatus("0"); // 设置为待入住
                                elder.setUpdateTime(new Date());
                                elderInfoService.updateElderInfo(elder);
                                logger.info("已恢复老人状态为待入住，老人ID：{}", order.getElderId());
                            } else {
                                logger.info("续费订单取消，不修改老人状态，老人ID：{}，当前状态：{}",
                                    order.getElderId(), elder.getStatus());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("恢复老人状态失败，老人ID：{}", order.getElderId(), e);
                        // 老人状态恢复失败不影响订单取消流程
                    }
                }

                logger.info("订单{}取消成功，订单号：{}", orderId, order.getOrderNo());
                return success("订单取消成功");
            } else {
                logger.error("订单{}取消失败，数据库更新返回0", orderId);
                return error("订单取消失败，请稍后重试");
            }
        } catch (Exception e) {
            logger.error("取消订单异常，订单ID：{}", orderId, e);
            return error("取消订单失败：" + e.getMessage());
        }
    }


    /**
     * 处理支付请求
     * 接收支付方式参数，创建支付记录
     */
    @PostMapping("/payment/process/{orderId}")
    public AjaxResult processPayment(@PathVariable Long orderId, @RequestParam(required = false) String paymentMethod) {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 查询订单信息
            OrderInfo order = orderInfoService.selectOrderInfoByOrderId(orderId);
            if (order == null) {
                return error("订单不存在");
            }

            // 验证订单权限 - 验证当前用户是否有权操作该订单关联的老人
            // 通过elder_family表验证用户是否为老人的家属或本人
            if (order.getElderId() != null && !hasElderAccess(currentUserId, order.getElderId())) {
                return error("无权操作该订单");
            }
            // 检查订单状态
            if ("1".equals(order.getOrderStatus())) {
                return error("订单已支付");
            }
            if ("2".equals(order.getOrderStatus())) {
                return error("���单已取消");
            }
            // 允许状态为 '0'（续费订单待支付）或 '5'（入驻订单审核通过待付款）的订单支付
            if (!"0".equals(order.getOrderStatus()) && !"5".equals(order.getOrderStatus())) {
                return error("订单状态异常，无法支付");
            }

            // 默认支付方式为现金
            if (!StringUtils.hasText(paymentMethod)) {
                paymentMethod = "现金";
            }

            // 支付方式映射：前端传的是英文，需要转换为中文
            String paymentMethodText = paymentMethod;
            if ("alipay".equals(paymentMethod)) {
                paymentMethod = "支付宝";
            } else if ("wechat".equals(paymentMethod)) {
                paymentMethod = "微信";
            }

            // 生成支付流水号
            String paymentNo = "PAY" + System.currentTimeMillis() + (int)(Math.random() * 1000);

            // 模拟支付处理
            try {
                // 模拟支付接口调用延迟
                Thread.sleep(1000);

                // 支付成功，更新订单状态
                order.setOrderStatus("1"); // 1-已支付
                order.setPaymentTime(new Date());
                order.setPaidAmount(order.getOrderAmount());
                order.setPaymentMethod(paymentMethod); // 更新支付方式
                orderInfoService.updateOrderInfo(order);

                // 创建支付记录
                PaymentRecord paymentRecord = new PaymentRecord();
                paymentRecord.setPaymentNo(paymentNo);
                paymentRecord.setOrderId(order.getOrderId());
                paymentRecord.setOrderNo(order.getOrderNo());
                paymentRecord.setElderId(order.getElderId());
                paymentRecord.setInstitutionId(order.getInstitutionId());
                paymentRecord.setPaymentAmount(order.getOrderAmount());
                paymentRecord.setPaymentMethod(paymentMethod);
                paymentRecord.setPaymentStatus("1"); // 1-成功
                paymentRecord.setPaymentTime(new Date());
                paymentRecord.setTransactionId(paymentNo); // 暂时使用流水号作为第三方交易号
                paymentRecord.setOperator(currentUserId.toString());
                paymentRecord.setElderName(order.getElderName() != null ? order.getElderName() : "未知老人");
                paymentRecord.setInstitutionName(order.getInstitutionId() != null ?
                    getInstitutionName(order.getInstitutionId()) : "未知机构");

                paymentRecordService.insertPaymentRecord(paymentRecord);

                // 记录监管账户流水
                try {
                    // 获取老人名称用于流水描述
                    String elderName = "未知老人";
                    if (order.getElderId() != null) {
                        ElderInfo elder = elderInfoService.selectElderInfoByElderId(order.getElderId());
                        if (elder != null && elder.getElderName() != null) {
                            elderName = elder.getElderName();
                        }
                    }

                    supervisionAccountLogService.recordIncome(
                        order.getInstitutionId(),
                        order.getOrderId(),
                        order.getOrderAmount(),
                        "用户支付订单-" + elderName,
                        currentUserId.toString()
                    );
                } catch (Exception e) {
                    logger.error("记录监管账户流水失败", e);
                    // 不影响支付流程，只记录错误
                }

                // 处理账户信息
                boolean accountUpdated = false;
                AccountInfo account = accountInfoService.selectAccountInfoByElderId(order.getElderId());

                if (account == null) {
                    // 创建新账户
                    account = accountInfoService.createAccountInfo(
                        order.getElderId(),
                        order.getInstitutionId(),
                        BigDecimal.ZERO
                    );
                }

                // 更新账户余额（基于订单金额分配）
                updateAccountBalanceFromOrder(account, order);
                accountUpdated = true;

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "支付成功");
                result.put("orderId", orderId);
                result.put("orderNo", order.getOrderNo());
                result.put("paymentTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date()));
                result.put("paidAmount", order.getPaidAmount());
                result.put("accountUpdated", accountUpdated);

                return success(result);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return error("支付处理被中断");
            }

        } catch (Exception e) {
            logger.error("处理支付请求失败", e);
            return error("支付失败：" + e.getMessage());
        }
    }

    /**
     * 根据订单信息更新账户余额
     */
    private void updateAccountBalanceFromOrder(AccountInfo account, OrderInfo order) {
        try {
            BigDecimal totalAmount = order.getOrderAmount() != null ? order.getOrderAmount() : BigDecimal.ZERO;

            // 根据订单明细项（order_item）的实际金额分配到不同余额类型
            BigDecimal serviceAmount = BigDecimal.ZERO;
            BigDecimal depositAmount = BigDecimal.ZERO;
            BigDecimal memberAmount = BigDecimal.ZERO;

            // 查询订单明细
            List<OrderItem> orderItems = orderItemService.selectOrderItemsByOrderId(order.getOrderId());

            if (orderItems != null && !orderItems.isEmpty()) {
                // 根据订单明细项的类型分配金额
                for (OrderItem item : orderItems) {
                    BigDecimal itemTotal = item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO;
                    String itemType = item.getItemType();

                    if ("deposit".equals(itemType)) {
                        // 押金类型
                        depositAmount = depositAmount.add(itemTotal);
                    } else if ("member_fee".equals(itemType)) {
                        // 会员费类型
                        memberAmount = memberAmount.add(itemTotal);
                    } else {
                        // 其他类型（床位费、护理费等）归入服务费
                        serviceAmount = serviceAmount.add(itemTotal);
                    }
                }
            } else {
                // 如果没有订单明细，使用旧的比例分配方式（向后兼容）
                String orderType = order.getOrderType();
                if ("1".equals(orderType)) { // 入驻订单
                    depositAmount = totalAmount.multiply(new BigDecimal("0.4")); // 40%押金
                    memberAmount = totalAmount.multiply(new BigDecimal("0.1"));  // 10%会员费
                    serviceAmount = totalAmount.subtract(depositAmount).subtract(memberAmount); // 剩余为服务费
                } else if ("2".equals(orderType)) { // 续费订单
                    serviceAmount = totalAmount.multiply(new BigDecimal("0.9")); // 90%服务费
                    memberAmount = totalAmount.multiply(new BigDecimal("0.1"));  // 10%其他费用
                } else { // 其他类型
                    serviceAmount = totalAmount.multiply(new BigDecimal("0.8")); // 80%服务费
                    depositAmount = totalAmount.multiply(new BigDecimal("0.2")); // 20%其他
                }
            }

            // 更新账户余额
            BigDecimal currentTotal = account.getTotalBalance() != null ? account.getTotalBalance() : BigDecimal.ZERO;
            BigDecimal currentService = account.getServiceBalance() != null ? account.getServiceBalance() : BigDecimal.ZERO;
            BigDecimal currentDeposit = account.getDepositBalance() != null ? account.getDepositBalance() : BigDecimal.ZERO;
            BigDecimal currentMember = account.getMemberBalance() != null ? account.getMemberBalance() : BigDecimal.ZERO;

            account.setTotalBalance(currentTotal.add(totalAmount));
            account.setServiceBalance(currentService.add(serviceAmount));
            account.setDepositBalance(currentDeposit.add(depositAmount));
            account.setMemberBalance(currentMember.add(memberAmount));
            account.setUpdateTime(new Date());
            account.setRemark(account.getRemark() + " | 支付订单" + order.getOrderNo() + "增加余额" + totalAmount + "元");

            accountInfoService.updateAccountInfo(account);

            // 生成订单支付费用记录
            try {
                int recordResult = expenseRecordService.createOrderExpenseRecords(
                    account.getElderId(),
                    account.getAccountId(),
                    order.getOrderId(),
                    order.getOrderType(),
                    depositAmount,
                    serviceAmount,
                    memberAmount,
                    BigDecimal.ZERO, // otherAmount 暂时为0
                    currentTotal,     // 支付前余额
                    account.getTotalBalance() // 支付后余额
                );
                if (recordResult <= 0) {
                    logger.warn("创建订单费用记录失败，但支付流程继续执行");
                } else {
                    // 费用记录生成成功后，如果是入驻订单，需要扣除首月服务费
                    if ("1".equals(order.getOrderType())) {
                        // 计算首月服务费并从账户余额中扣除
                        BigDecimal firstMonthServiceFee = calculateFirstMonthServiceFee(order.getOrderId());
                        if (firstMonthServiceFee.compareTo(BigDecimal.ZERO) > 0) {
                            // 检查服务费余额是否足够
                            if (account.getServiceBalance().compareTo(firstMonthServiceFee) >= 0) {
                                BigDecimal newServiceBalance = account.getServiceBalance().subtract(firstMonthServiceFee);
                                BigDecimal newTotalBalance = account.getTotalBalance().subtract(firstMonthServiceFee);

                                account.setServiceBalance(newServiceBalance);
                                account.setTotalBalance(newTotalBalance);

                                // 再次更新账户余额（扣除首月服务费）
                                accountInfoService.updateAccountInfo(account);

                                // 生成监管账户划拨流水记录（首月服务费划拨到机构基本账户）
                                try {
                                    supervisionAccountLogService.recordTransferOut(
                                        order.getInstitutionId(),
                                        order.getOrderId(),
                                        firstMonthServiceFee,
                                        "首月服务费划拨-" + order.getOrderNo(),
                                        "基本账户"
                                    );
                                    logger.info("生成首月服务费划拨流水：" + firstMonthServiceFee + "元，订单号：" + order.getOrderNo());
                                } catch (Exception e) {
                                    logger.error("记录首月服务费划拨流水失败", e);
                                    // 划拨流水记录失败不影响主流程
                                }

                                logger.info("扣除首月服务费：" + firstMonthServiceFee + "元，订单号：" + order.getOrderNo());

                                // 生成首月服务费立即划拨的拨付单（已完成状态）
                                try {
                                    createFirstMonthTransfer(order, firstMonthServiceFee);
                                    logger.info("生成首月服务费拨付单成功，订单号：" + order.getOrderNo());
                                } catch (Exception e) {
                                    logger.error("生成首月服务费拨付单失败", e);
                                }

                                // 生成后续月份的拨付单（从次月开始）
                                try {
                                    Integer monthCount = order.getMonthCount() != null ? order.getMonthCount() : 1;
                                    // 如果只有1个月，则不生成后续拨付单
                                    if (monthCount > 1) {
                                        fundTransferService.generateMonthlyTransfersForOrder(
                                            order.getOrderId(),
                                            order.getInstitutionId(),
                                            order.getElderId(),
                                            monthCount - 1, // 首月已处理，生成剩余月份
                                            order.getServiceStartDate() != null ? order.getServiceStartDate() : new Date(),
                                            firstMonthServiceFee, // 使用首月服务费作为月费参考
                                            false // 从次月开始
                                        );
                                        logger.info("生成后续月份拨付单成功，月数：" + (monthCount - 1) + "，订单号：" + order.getOrderNo());
                                    }
                                } catch (Exception e) {
                                    logger.error("生成后续月份拨付单失败", e);
                                }
                            } else {
                                logger.warn("服务费余额不足以扣除首月服务费：" + firstMonthServiceFee + "元，当前余额：" + account.getServiceBalance() + "元");
                            }
                        }
                    }
                    // 续费订单处理逻辑
                    else if ("2".equals(order.getOrderType())) {
                        try {
                            Integer monthCount = order.getMonthCount() != null ? order.getMonthCount() : 1;

                            // 1. 更新床位分配的到期日期
                            if (monthCount > 0 && order.getServiceEndDate() != null) {
                                BedAllocation bedAllocation = bedAllocationMapper.selectBedAllocationByElderId(order.getElderId());
                                if (bedAllocation != null) {
                                    Date newDueDate = order.getServiceEndDate();
                                    bedAllocation.setDueDate(newDueDate);
                                    bedAllocation.setUpdateTime(new Date());
                                    bedAllocationMapper.updateBedAllocation(bedAllocation);
                                    logger.info("续费订单到期日期已更新：" + DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, newDueDate) + "，订单号：" + order.getOrderNo());
                                }
                            }

                            // 2. 计算正确的月服务费（不含押金和会员费）
                            BigDecimal monthlyServiceFee = calculateRenewMonthlyFee(order.getOrderId());

                            // 3. 生成续费月份的拨付单（从次月开始）
                            if (monthCount > 0 && monthlyServiceFee.compareTo(BigDecimal.ZERO) > 0) {
                                fundTransferService.generateMonthlyTransfersForOrder(
                                    order.getOrderId(),
                                    order.getInstitutionId(),
                                    order.getElderId(),
                                    monthCount,
                                    order.getServiceStartDate() != null ? order.getServiceStartDate() : new Date(),
                                    monthlyServiceFee,
                                    false // 从次月开始，不跳过首月
                                );

                                logger.info("生成续费订单拨付单成功，月数：" + monthCount + "，月服务费：" + monthlyServiceFee + "，订单号：" + order.getOrderNo());
                            }
                        } catch (Exception e) {
                            logger.error("续费订单后续处理失败", e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("创建订单费用记录异常：" + e.getMessage());
                // 费用记录创建失败不影响主流程
            }

        } catch (Exception e) {
            logger.error("更新账户余额失败", e);
            throw new RuntimeException("更新账户余额失败：" + e.getMessage());
        }
    }

    /**
     * 创建首月服务费立即划拨的拨付单
     *
     * @param order 订单信息
     * @param amount 划拨金额
     */
    private void createFirstMonthTransfer(OrderInfo order, BigDecimal amount) {
        com.ruoyi.domain.pension.FundTransfer transfer = new com.ruoyi.domain.pension.FundTransfer();
        transfer.setInstitutionId(order.getInstitutionId());
        transfer.setElderId(order.getElderId());
        transfer.setOrderId(order.getOrderId());

        // 生成拨付单号
        String transferNo = "TRF" + System.currentTimeMillis() + "FM" + String.format("%02d", (int)(Math.random() * 100));
        transfer.setTransferNo(transferNo);
        transfer.setTransferType("1"); // 自动划拨
        transfer.setTransferAmount(amount);
        transfer.setTransferDate(new Date());

        // 设置当前月份为划拨周期
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        String currentPeriod = sdf.format(new Date());
        transfer.setTransferPeriod(currentPeriod);
        transfer.setBillingMonth(currentPeriod);

        transfer.setElderCount(1);
        transfer.setTransferStatus("1"); // 已完成
        transfer.setIsPaid("1"); // 已划拨
        transfer.setStatus("completed"); // 已完成
        transfer.setExecuteUser("system");
        transfer.setExecuteTime(new Date());
        transfer.setPaidTime(new Date()); // 设置实际划拨时间
        transfer.setPaidMethod("auto");
        transfer.setCreateBy("system");
        transfer.setCreateTime(new Date());
        transfer.setRemark("首月服务费立即划拨-" + order.getOrderNo());

        fundTransferService.insertFundTransfer(transfer);
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
     * 获取机构可用的餐费配置列表
     */
    @GetMapping("/meal/available/{institutionId}")
    public AjaxResult getAvailableMeals(@PathVariable Long institutionId)
    {
        try {
            MealFeeConfig query = new MealFeeConfig();
            query.setInstitutionId(institutionId);
            query.setIsAvailable("1"); // 只查询启用的

            List<MealFeeConfig> mealList = mealFeeConfigService.selectMealFeeConfigList(query);
            return success(mealList);
        } catch (Exception e) {
            logger.error("获取餐费配置失败", e);
            return error("获取餐费配置失败");
        }
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
            // 转换中文护理等级为数字代码（elder_info.care_level 为 char(1)，只能存 1/2/3）
            String careLevelCode;
            switch (careLevel) {
                case "半护理": careLevelCode = "2"; break;
                case "全护理": careLevelCode = "3"; break;
                default:       careLevelCode = "1"; break; // 自理
            }
            Integer monthCount = Integer.valueOf(orderData.get("monthCount").toString());

            // 获取餐费档次
            String mealLevelCode = orderData.get("mealLevelCode") != null
                ? orderData.get("mealLevelCode").toString()
                : null;

            // 验证餐费配置
            if (mealLevelCode == null) {
                return error("请选择餐费档次");
            }

            // 查询餐费配置
            MealFeeConfig mealConfig = mealFeeConfigService.selectMealByLevelCode(institutionId, mealLevelCode);
            if (mealConfig == null) {
                return error("该机构暂未配置餐费，无法提交订单");
            }

            BigDecimal mealFee = mealConfig.getPrice(); // 餐费

            // 验证老人信息
            ElderInfo elder = elderInfoService.selectElderInfoByElderId(elderId);
            if (elder == null) {
                return error("抱歉，未找到该��人信息。请重新选择或联系客服。");
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
            checkinDTO.setCareLevel(careLevelCode); // 存数字代码（char(1)），中文用于费用计算
            checkinDTO.setHealthStatus(elder.getHealthStatus());
            checkinDTO.setEmergencyContact(elder.getEmergencyContact());
            checkinDTO.setEmergencyPhone(elder.getEmergencyPhone());

            // 床位信息
            checkinDTO.setBedId(selectedBed.getBedId());
            checkinDTO.setCheckInDate(new Date()); // 默认今天入住

            // 费用计算（服务费 = 床位费 + 护理费 + 餐费）
            BigDecimal bedFee = selectedBed.getPrice(); // 床位费
            BigDecimal careFee = getCareFeeByLevel(selectedBed, careLevel); // 护理费
            BigDecimal monthlyFee = bedFee.add(careFee).add(mealFee); // 月服务费 = 床位费 + 护理费 + 餐费
            BigDecimal depositAmount = selectedBed.getDepositFee() != null ? selectedBed.getDepositFee() : new BigDecimal("10000");
            BigDecimal memberFee = selectedBed.getMemberFee() != null ? selectedBed.getMemberFee() : new BigDecimal("5000");
            BigDecimal finalAmount = monthlyFee.multiply(new BigDecimal(monthCount))
                                          .add(depositAmount)
                                          .add(memberFee);

            checkinDTO.setMonthlyFee(monthlyFee);
            checkinDTO.setMonthCount(monthCount);
            checkinDTO.setDepositAmount(depositAmount);
            checkinDTO.setMemberFee(memberFee);
            checkinDTO.setMealFee(mealFee);
            checkinDTO.setMealLevel(mealConfig.getMealLevel());
            checkinDTO.setFinalAmount(finalAmount);
            checkinDTO.setFeeDescription(buildFeeDescription(bedFee, careFee, mealFee, mealConfig.getMealLevel(), monthlyFee, depositAmount, memberFee, monthCount));

            // 支付方式（默认稍后支付）
            checkinDTO.setPaymentMethod("later");
            checkinDTO.setRemark("H5小程序订单来源，餐费档次：" + mealConfig.getMealLevel());

            // 创建入住申请 - 使用当前登录用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录，请先登录后再提交订单");
            }
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

                    // 将订单状态设置为待审核（4），等待管理员审核
                    if (latestOrder != null) {
                        latestOrder.setOrderStatus("4"); // 4-待审核
                        orderInfoService.updateOrderInfo(latestOrder);
                    }
                }

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "订单提交成功，请等待管理员审核");

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
     * @param careLevel 护理等级（支持中文：自理/半护理/全护理 或 数字：1/2/3）
     * @return 护理费
     */
    private BigDecimal getCareFeeByLevel(BedInfo bed, String careLevel) {
        if (careLevel == null) {
            return bed.getSelfCarePrice() != null ? bed.getSelfCarePrice() : new BigDecimal("500");
        }

        // 支持中文和数字两种格式
        switch (careLevel) {
            case "1":
            case "自理":
                return bed.getSelfCarePrice() != null ? bed.getSelfCarePrice() : new BigDecimal("500");
            case "2":
            case "半护理":
                return bed.getHalfCarePrice() != null ? bed.getHalfCarePrice() : new BigDecimal("800");
            case "3":
            case "全护理":
                return bed.getFullCarePrice() != null ? bed.getFullCarePrice() : new BigDecimal("1200");
            default:
                return bed.getSelfCarePrice() != null ? bed.getSelfCarePrice() : new BigDecimal("500");
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
    private String buildFeeDescription(BigDecimal bedFee, BigDecimal careFee, BigDecimal mealFee, String mealLevelName,
                                     BigDecimal monthlyFee, BigDecimal depositAmount, BigDecimal memberFee,
                                     Integer monthCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("床位费：").append(bedFee).append("元/月\n");
        sb.append("护理费：").append(careFee).append("元/月\n");
        sb.append("餐费：").append(mealFee).append("元/月\n");
        if (mealFee.compareTo(BigDecimal.ZERO) > 0 && mealLevelName != null && !mealLevelName.isEmpty()) {
            sb.append("餐费档次：").append(mealLevelName).append("\n");
        }
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

    /**
     * 获取订单类型文本
     */
    private String getOrderTypeText(String orderType) {
        switch (orderType) {
            case "1":
                return "入驻";
            case "2":
                return "续费";
            default:
                return "未知";
        }
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(String orderStatus) {
        switch (orderStatus) {
            case "0":
                return "待支付";
            case "1":
                return "已支付";
            case "2":
                return "已取消";
            case "3":
                return "已退款";
            case "4":
                return "等待机构审核";
            case "5":
                return "待付款";
            default:
                return "未知";
        }
    }

    /**
     * 根据机构ID获取机构名称
     */
    private String getInstitutionName(Long institutionId) {
        try {
            PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(institutionId);
            if (institution != null) {
                return institution.getInstitutionName();
            }
        } catch (Exception e) {
            logger.warn("获取机构名称失败，institutionId=" + institutionId, e);
        }
        return "未知机构";
    }

    /**
     * 获取当前登录用户的ID
     * H5端必须登录才能查看订单
     */
    private Long getCurrentUserId() {
        try {
            // 从SecurityUtils获取已登录用户
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            // 用户未登录，返回null
            logger.warn("获取当前用户ID失败，用户可能未登录", e);
            return null;
        }
    }

    // ========================= 费用记录相关API =========================

    /**
     * 获取老人费用记录列表
     * H5端用户只能查看自己关联老人的费用记录
     *
     * 数据说明：
     * - 收入：从 expense_record 查询 transaction_type='income' 的记录
     * - 支出（服务费）：从 fund_transfer 查询 paid_method IN ('auto','manual')
     * - 支出（押金）：从 fund_transfer 查询 paid_method='deposit'
     *
     * 费用类型说明：
     * - service（服务费）：expense_record(service,income) + fund_transfer(auto,manual,expense)
     * - deposit（押金）：expense_record(deposit,income) + fund_transfer(deposit,expense)
     * - member（会员费）：从 expense_record 查询
     * - all：合并所有类型的数据
     */
    @GetMapping("/expense/list")
    public AjaxResult getExpenseList(@RequestParam Long elderId,
                                    @RequestParam(required = false, defaultValue = "all") String type,
                                    @RequestParam(required = false, defaultValue = "all") String transactionType,
                                    @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                    @RequestParam(required = false, defaultValue = "20") Integer pageSize)
    {
        try {
            // 验证用户登录
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 验证老人权限（只能查看自己关联的老人）
            if (!hasElderAccess(userId, elderId)) {
                return error("您没有权限查看该老人的费用记录");
            }

            // 存储所有费用记录（统一转换为Map格式返回）
            List<Map<String, Object>> allRecords = new java.util.ArrayList<>();

            // ========== 1. 查询收入记录（从 expense_record） ==========
            if ("all".equals(transactionType) || "income".equals(transactionType)) {
                ExpenseRecord incomeQuery = new ExpenseRecord();
                incomeQuery.setElderId(elderId);
                incomeQuery.setTransactionType("income"); // 只查收入

                // 费用类型过滤
                if ("service".equals(type)) {
                    incomeQuery.setExpenseType("service");
                } else if ("deposit".equals(type)) {
                    incomeQuery.setExpenseType("deposit");
                } else if ("member".equals(type)) {
                    incomeQuery.setExpenseType("member");
                }

                List<ExpenseRecord> incomeRecords = expenseRecordService.selectExpenseRecordList(incomeQuery);
                for (ExpenseRecord er : incomeRecords) {
                    Map<String, Object> record = new java.util.HashMap<>();
                    record.put("recordId", er.getRecordId());
                    record.put("expenseType", er.getExpenseType());
                    record.put("amount", er.getAmount());
                    record.put("createTime", er.getCreateTime());
                    record.put("transactionType", "income");
                    record.put("description", er.getDescription());
                    record.put("balanceBefore", er.getBalanceBefore());
                    record.put("balanceAfter", er.getBalanceAfter());
                    allRecords.add(record);
                }
            }

            // ========== 2. 查询支出记录（从 fund_transfer） ==========
            if ("all".equals(transactionType) || "expense".equals(transactionType)) {
                // 服务费划拨（支出）
                if ("all".equals(type) || "service".equals(type)) {
                    List<com.ruoyi.domain.pension.FundTransfer> serviceTransfers =
                        fundTransferService.selectByElderIdAndPaidMethods(elderId, new String[]{"auto", "manual"});
                    for (com.ruoyi.domain.pension.FundTransfer ft : serviceTransfers) {
                        Map<String, Object> record = new java.util.HashMap<>();
                        record.put("recordId", ft.getTransferId());
                        record.put("expenseType", "service");
                        record.put("amount", ft.getTransferAmount());
                        record.put("createTime", ft.getPaidTime());
                        record.put("transactionType", "expense");
                        record.put("description", ft.getRemark() != null ? ft.getRemark() : "服务费划拨");
                        record.put("balanceBefore", null);
                        record.put("balanceAfter", null);
                        allRecords.add(record);
                    }
                }

                // 押金划拨（支出）
                if ("all".equals(type) || "deposit".equals(type)) {
                    List<com.ruoyi.domain.pension.FundTransfer> depositTransfers =
                        fundTransferService.selectByElderIdAndPaidMethods(elderId, new String[]{"deposit"});
                    for (com.ruoyi.domain.pension.FundTransfer ft : depositTransfers) {
                        Map<String, Object> record = new java.util.HashMap<>();
                        record.put("recordId", ft.getTransferId());
                        record.put("expenseType", "deposit");
                        record.put("amount", ft.getTransferAmount());
                        record.put("createTime", ft.getPaidTime());
                        record.put("transactionType", "expense");
                        record.put("description", ft.getRemark() != null ? ft.getRemark() : "押金使用划拨");
                        record.put("balanceBefore", null);
                        record.put("balanceAfter", null);
                        allRecords.add(record);
                    }
                }
            }

            // 按时间正序排序（最早的在前）
            allRecords.sort((a, b) -> {
                java.util.Date timeA = (java.util.Date) a.get("createTime");
                java.util.Date timeB = (java.util.Date) b.get("createTime");
                if (timeA == null) return 1;
                if (timeB == null) return -1;
                return timeA.compareTo(timeB);
            });

            // 简单分页处理
            int total = allRecords.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Map<String, Object>> pageList = start < total ?
                allRecords.subList(Math.max(0, start), Math.min(end, total)) :
                new java.util.ArrayList<>();

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("pages", (total + pageSize - 1) / pageSize);
            result.put("list", pageList);

            return success(result);

        } catch (Exception e) {
            logger.error("获取费用记录列表失败", e);
            return error("获取费用记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取费用记录详情
     */
    @GetMapping("/expense/{recordId}")
    public AjaxResult getExpenseDetail(@PathVariable Long recordId) {
        try {
            // 验证用户登录
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 查询费用记录
            ExpenseRecord expenseRecord = expenseRecordService.selectExpenseRecordByRecordId(recordId);
            if (expenseRecord == null) {
                return error("费用记录不存在");
            }

            // 验证老人权限（只能查看自己关联的老人）
            if (!hasElderAccess(userId, expenseRecord.getElderId())) {
                return error("您没有权限查看该费用记录");
            }

            return success(expenseRecord);

        } catch (Exception e) {
            logger.error("获取费用记录详情失败", e);
            return error("获取费用记录详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取老人费用统计信息
     */
    @GetMapping("/expense/statistics")
    public AjaxResult getExpenseStatistics(@RequestParam Long elderId) {
        try {
            // 验证用户登录
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 验证老人权限
            if (!hasElderAccess(userId, elderId)) {
                return error("您没有权限查看该老人的费用统计");
            }

            // 查询所有费用记录
            ExpenseRecord query = new ExpenseRecord();
            query.setElderId(elderId);
            List<ExpenseRecord> allRecords = expenseRecordService.selectExpenseRecordList(query);

            // 统计计算
            Map<String, Object> statistics = new HashMap<>();

            // 按费用类型统计
            Map<String, BigDecimal> typeAmount = new HashMap<>();
            Map<String, Long> typeCount = new HashMap<>();

            // 按交易类型统计
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;

            for (ExpenseRecord record : allRecords) {
                String expType = record.getExpenseType();
                String transType = record.getTransactionType();
                BigDecimal amount = record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO;

                // 按费用类型统计
                typeAmount.put(expType, typeAmount.getOrDefault(expType, BigDecimal.ZERO).add(amount));
                typeCount.put(expType, typeCount.getOrDefault(expType, 0L) + 1);

                // 按交易类型统计
                if ("income".equals(transType)) {
                    totalIncome = totalIncome.add(amount);
                } else if ("expense".equals(transType)) {
                    totalExpense = totalExpense.add(amount);
                }
            }

            statistics.put("typeAmount", typeAmount);
            statistics.put("typeCount", typeCount);
            statistics.put("totalIncome", totalIncome);
            statistics.put("totalExpense", totalExpense);
            statistics.put("netAmount", totalIncome.subtract(totalExpense));
            statistics.put("totalCount", allRecords.size());

            return success(statistics);

        } catch (Exception e) {
            logger.error("获取费用统计信息失败", e);
            return error("获取费用统计失败：" + e.getMessage());
        }
    }

    /**
     * 验证用户是否有权限访问老人信息
     * 通过elder_family表验证用户是否为老人的家属
     */
    private boolean hasElderAccess(Long userId, Long elderId) {
        try {
            ElderFamily query = new ElderFamily();
            query.setUserId(userId);
            query.setElderId(elderId);

            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(query);
            return familyList != null && !familyList.isEmpty();
        } catch (Exception e) {
            logger.error("验证老人访问权限失败", e);
            return false;
        }
    }

    /**
     * 计算首月服务费
     * 根据订单明细中的床位费、护理费和餐费计算首月费用
     *
     * @param orderId 订单ID
     * @return 首月服务费金额
     */
    private BigDecimal calculateFirstMonthServiceFee(Long orderId) {
        try {
            List<OrderItem> orderItems = orderItemService.selectOrderItemsByOrderId(orderId);
            BigDecimal firstMonthFee = BigDecimal.ZERO;

            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItem item : orderItems) {
                    String itemType = item.getItemType();
                    BigDecimal totalAmount = item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 1;

                    if ("bed_fee".equals(itemType) || "care_fee".equals(itemType) || "meal_fee".equals(itemType)) {
                        // 计算单月费用：总金额 / 数量（月份数）
                        if (quantity > 0) {
                            BigDecimal monthlyFee = totalAmount.divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP);
                            firstMonthFee = firstMonthFee.add(monthlyFee);
                        }
                    }
                }
            }

            return firstMonthFee;
        } catch (Exception e) {
            // 计算失败时返回0，避免影响主流程
            logger.error("计算首月服务费异常：" + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // ========================= 续费功能 =========================

    /**
     * 获取老人续费信息
     * 用于续费页面展示老人当前状态、价格、到期日期等
     */
    @GetMapping("/order/renew/info/{elderId}")
    public AjaxResult getRenewInfo(@PathVariable("elderId") Long elderId) {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 验证用户是否有权限访问该老人信息
            if (!hasElderAccess(currentUserId, elderId)) {
                return error("您没有权限访问该老人的信息");
            }

            // 查询老人基本信息
            ElderInfo elder = elderInfoService.selectElderInfoByElderId(elderId);
            if (elder == null) {
                return error("老人信息不存在");
            }

            // 查询床位分配信息（获取到期日期、床位信息）
            BedAllocation bedAllocation = bedAllocationMapper.selectBedAllocationByElderId(elderId);
            if (bedAllocation == null) {
                return error("未找到床位分配信息，无法续费");
            }

            // 查询床位信息（获取价格）
            BedInfo bedInfo = bedInfoService.selectBedInfoByBedId(bedAllocation.getBedId());
            if (bedInfo == null) {
                return error("未找到床位信息");
            }

            // 获取当前价格
            ElderCurrentPriceVO currentPrice = residentService.getCurrentPrice(elderId);

            // 查询账户余额
            AccountInfo account = accountInfoService.selectAccountInfoByElderId(elderId);

            // 获取机构可用的餐费配置列表
            List<MealFeeConfig> mealConfigList = null;
            if (bedAllocation.getInstitutionId() != null) {
                MealFeeConfig mealQuery = new MealFeeConfig();
                mealQuery.setInstitutionId(bedAllocation.getInstitutionId());
                mealQuery.setIsAvailable("1");
                mealConfigList = mealFeeConfigService.selectMealFeeConfigList(mealQuery);
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();

            // 老人信息
            result.put("elderId", elder.getElderId());
            result.put("elderName", elder.getElderName());
            result.put("careLevel", elder.getCareLevel());
            result.put("careLevelText", getCareLevelText(elder.getCareLevel()));
            result.put("mealLevelCode", elder.getMealLevelCode());

            // 床位信息
            result.put("bedId", bedInfo.getBedId());
            result.put("roomNumber", bedInfo.getRoomNumber() != null ? bedInfo.getRoomNumber() : "");
            result.put("bedNumber", bedInfo.getBedNumber() != null ? bedInfo.getBedNumber() : "");
            result.put("bedInfo", (bedInfo.getRoomNumber() != null ? bedInfo.getRoomNumber() : "")
                + "-" + (bedInfo.getBedNumber() != null ? bedInfo.getBedNumber() : ""));

            // 到期日期
            result.put("currentDueDate", bedAllocation.getDueDate());
            result.put("currentDueDateText", bedAllocation.getDueDate() != null ?
                DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, bedAllocation.getDueDate()) : "");

            // 当前价格
            result.put("bedFee", currentPrice.getBedFee() != null ? currentPrice.getBedFee() : BigDecimal.ZERO);
            result.put("careFee", currentPrice.getCareFee() != null ? currentPrice.getCareFee() : BigDecimal.ZERO);
            result.put("mealFee", currentPrice.getMealFee() != null ? currentPrice.getMealFee() : BigDecimal.ZERO);
            result.put("monthlyFeeTotal", currentPrice.getMonthlyFeeTotal() != null ? currentPrice.getMonthlyFeeTotal() : BigDecimal.ZERO);
            result.put("depositFee", currentPrice.getDepositFee() != null ? currentPrice.getDepositFee() : BigDecimal.ZERO);
            result.put("memberFee", currentPrice.getMemberFee() != null ? currentPrice.getMemberFee() : BigDecimal.ZERO);

            // 护理等级价格
            result.put("selfCarePrice", bedInfo.getSelfCarePrice() != null ? bedInfo.getSelfCarePrice() : BigDecimal.ZERO);
            result.put("halfCarePrice", bedInfo.getHalfCarePrice() != null ? bedInfo.getHalfCarePrice() : BigDecimal.ZERO);
            result.put("fullCarePrice", bedInfo.getFullCarePrice() != null ? bedInfo.getFullCarePrice() : BigDecimal.ZERO);

            // 账户余额
            if (account != null) {
                result.put("serviceBalance", account.getServiceBalance() != null ? account.getServiceBalance() : BigDecimal.ZERO);
                result.put("depositBalance", account.getDepositBalance() != null ? account.getDepositBalance() : BigDecimal.ZERO);
                result.put("memberBalance", account.getMemberBalance() != null ? account.getMemberBalance() : BigDecimal.ZERO);
                result.put("totalBalance", account.getTotalBalance() != null ? account.getTotalBalance() : BigDecimal.ZERO);
            } else {
                result.put("serviceBalance", BigDecimal.ZERO);
                result.put("depositBalance", BigDecimal.ZERO);
                result.put("memberBalance", BigDecimal.ZERO);
                result.put("totalBalance", BigDecimal.ZERO);
            }

            // 机构信息
            result.put("institutionId", bedAllocation.getInstitutionId());
            PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(bedAllocation.getInstitutionId());
            if (institution != null) {
                result.put("institutionName", institution.getInstitutionName());
            }

            // 餐费配置
            result.put("mealConfigList", mealConfigList);

            return success(result);
        } catch (Exception e) {
            logger.error("获取续费信息失败", e);
            return error("获取续费信息失败：" + e.getMessage());
        }
    }

    /**
     * 提交续费订单
     * 创建续费订单，状态为待支付，用户需要跳转到支付页面完成支付
     */
    @PostMapping("/order/renew")
    public AjaxResult submitRenewOrder(@RequestBody Map<String, Object> renewData) {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 解析参数
            Long elderId = Long.valueOf(renewData.get("elderId").toString());
            Integer monthCount = Integer.valueOf(renewData.get("monthCount").toString());
            String careLevel = renewData.get("careLevel") != null ? renewData.get("careLevel").toString() : null;
            String mealLevelCode = renewData.get("mealLevelCode") != null ? renewData.get("mealLevelCode").toString() : null;
            BigDecimal depositAmount = renewData.get("depositAmount") != null ?
                new BigDecimal(renewData.get("depositAmount").toString()) : BigDecimal.ZERO;
            BigDecimal memberFee = renewData.get("memberFee") != null ?
                new BigDecimal(renewData.get("memberFee").toString()) : BigDecimal.ZERO;
            String remark = renewData.get("remark") != null ? renewData.get("remark").toString() : "";

            // 验证用户是否有权限访问该老人信息
            if (!hasElderAccess(currentUserId, elderId)) {
                return error("您没有权限为该老人续费");
            }

            // 构建续费DTO
            RenewDTO renewDTO = new RenewDTO();
            renewDTO.setElderId(elderId);
            renewDTO.setMonthCount(monthCount);
            renewDTO.setCareLevel(careLevel);
            renewDTO.setMealLevelCode(mealLevelCode);
            renewDTO.setDepositAmount(depositAmount);
            renewDTO.setMemberFee(memberFee);
            renewDTO.setPaymentMethod("online"); // 用户端支付
            renewDTO.setRemark(remark);

            // 调用续费服务
            int result = residentService.renewResident(renewDTO, currentUserId);

            if (result > 0) {
                // 查询刚创建的续费订单
                OrderInfo orderQuery = new OrderInfo();
                orderQuery.setElderId(elderId);
                orderQuery.setOrderType("2"); // 续费订单
                orderQuery.setOrderStatus("0"); // 待支付
                orderQuery.setOrderDate(new Date());
                List<OrderInfo> orderList = orderInfoService.selectOrderInfoList(orderQuery);

                if (orderList != null && !orderList.isEmpty()) {
                    // 找到最新创建的订单
                    OrderInfo renewOrder = orderList.stream()
                        .max((o1, o2) -> o1.getOrderId().compareTo(o2.getOrderId()))
                        .orElse(null);

                    if (renewOrder != null) {
                        Map<String, Object> responseData = new HashMap<>();
                        responseData.put("success", true);
                        responseData.put("message", "续费订单创建成功，请完成支付");
                        responseData.put("orderId", renewOrder.getOrderId());
                        responseData.put("orderNo", renewOrder.getOrderNo());
                        responseData.put("orderAmount", renewOrder.getOrderAmount());
                        return success(responseData);
                    }
                }

                return success("续费订单创建成功");
            } else {
                return error("续费订单创建失败");
            }
        } catch (Exception e) {
            logger.error("提交续费订单失败", e);
            return error("提交续费订单失败：" + e.getMessage());
        }
    }

    /**
     * 计算续费订单的月服务费
     * 根据订单明细计算纯服务费（不含押金和会员费）
     *
     * @param orderId 订单ID
     * @return 月服务费
     */
    private BigDecimal calculateRenewMonthlyFee(Long orderId) {
        try {
            List<OrderItem> orderItems = orderItemService.selectOrderItemsByOrderId(orderId);
            BigDecimal serviceFeeTotal = BigDecimal.ZERO;
            Integer monthCount = 1;

            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItem item : orderItems) {
                    String itemType = item.getItemType();
                    BigDecimal totalAmount = item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 1;

                    // 累加服务费（床位费、护理费、餐费）
                    if ("bed_fee".equals(itemType) || "care_fee".equals(itemType) || "meal_fee".equals(itemType)) {
                        serviceFeeTotal = serviceFeeTotal.add(totalAmount);
                        if ("bed_fee".equals(itemType)) {
                            monthCount = quantity; // 使用床位费的月数作为总月数
                        }
                    }
                }
            }

            // 计算月服务费
            if (monthCount > 0) {
                return serviceFeeTotal.divide(new BigDecimal(monthCount), 2, BigDecimal.ROUND_HALF_UP);
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("计算续费月服务费异常：" + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}