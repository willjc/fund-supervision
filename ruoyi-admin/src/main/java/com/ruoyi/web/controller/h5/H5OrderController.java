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
import com.ruoyi.domain.PensionCheckinDTO;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.service.IOrderInfoService;
import com.ruoyi.service.IPensionCheckinService;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.service.pension.IDepositApplyService;

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
    private IElderFamilyService elderFamilyService;

    @Autowired
    private IPensionInstitutionService institutionService;

    @Autowired
    private IPensionInstitutionPublicService institutionPublicService;

    @Autowired
    private IAccountInfoService accountInfoService;

    @Autowired
    private IDepositApplyService depositApplyService;

    /**
     * 获取订单列表
     * 根据当前登录用户查询该用户创建的所有订单
     */
    @GetMapping("/order/list")
    public AjaxResult getOrderList(@RequestParam(required = false) Long elderId,
                                   @RequestParam(required = false) String orderStatus,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize)
    {
        try {
            // 获取当前用户身份
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 构建查询条件 - 直接根据当前用户ID查询订单
            OrderInfo query = new OrderInfo();
            query.setCreatorUserId(currentUserId);

            // 如果前端传入了elderId参数，同时按老人ID筛选
            if (elderId != null && elderId > 0) {
                query.setElderId(elderId);
            }

            // 如果提供了订单状态，按状态查询
            if (orderStatus != null && !orderStatus.isEmpty() && !"all".equals(orderStatus)) {
                query.setOrderStatus(orderStatus);
            }

            // 查询订单列表
            List<OrderInfo> orderList = orderInfoService.selectOrderInfoList(query);

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
     */
    @GetMapping("/account/{elderId}")
    public AjaxResult getAccountInfo(@PathVariable Long elderId) {
        try {
            // 获取当���用户ID
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
                defaultAccount.put("totalBalance", new BigDecimal("0"));
                defaultAccount.put("serviceBalance", new BigDecimal("0"));
                defaultAccount.put("depositBalance", new BigDecimal("0"));
                defaultAccount.put("memberBalance", new BigDecimal("0"));
                defaultAccount.put("prepaidAmount", new BigDecimal("0"));
                defaultAccount.put("hasAccount", false);
                return success(defaultAccount);
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("accountId", account.getAccountId());
            result.put("accountNo", account.getAccountNo());
            result.put("totalBalance", account.getTotalBalance() != null ? account.getTotalBalance() : new BigDecimal("0"));
            result.put("serviceBalance", account.getServiceBalance() != null ? account.getServiceBalance() : new BigDecimal("0"));
            result.put("depositBalance", account.getDepositBalance() != null ? account.getDepositBalance() : new BigDecimal("0"));
            result.put("memberBalance", account.getMemberBalance() != null ? account.getMemberBalance() : new BigDecimal("0"));

            // 计算预存金额（总余额 - 押金余额）
            BigDecimal totalBalance = account.getTotalBalance() != null ? account.getTotalBalance() : new BigDecimal("0");
            BigDecimal depositBalance = account.getDepositBalance() != null ? account.getDepositBalance() : new BigDecimal("0");
            BigDecimal prepaidAmount = totalBalance.subtract(depositBalance);
            result.put("prepaidAmount", prepaidAmount.compareTo(BigDecimal.ZERO) >= 0 ? prepaidAmount : new BigDecimal("0"));
            result.put("hasAccount", true);

            return success(result);
        } catch (Exception e) {
            logger.error("获取账户信息失败", e);
            return error("获取账户信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取老人费用明细记录
     */
    @GetMapping("/expense/list")
    public AjaxResult getExpenseList(@RequestParam Long elderId,
                                   @RequestParam(required = false, defaultValue = "all") String type,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "20") Integer pageSize) {
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

            List<Map<String, Object>> expenseList = new java.util.ArrayList<>();

            // 1. 查询押金使用记录（已批准的申请）
            if ("all".equals(type) || "deposit".equals(type)) {
                DepositApply depositQuery = new DepositApply();
                depositQuery.setElderId(elderId);
                List<DepositApply> depositList = depositApplyService.selectDepositApplyList(depositQuery);

                for (DepositApply deposit : depositList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", "DEP_" + deposit.getApplyId());
                    item.put("type", "expense");
                    item.put("title", "押金使用");
                    item.put("amount", deposit.getActualAmount() != null ? deposit.getActualAmount() : deposit.getApplyAmount());
                    item.put("time", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, deposit.getCreateTime()));
                    item.put("description", deposit.getApplyReason());

                    // 只有已批准且已使用的才能申请退款
                    boolean canRefund = "approved".equals(deposit.getApplyStatus()) && deposit.getUseTime() != null;
                    item.put("canRefund", canRefund);

                    expenseList.add(item);
                }
            }

            // 2. 查询服务费相关记录（基于订单）
            if ("all".equals(type) || "service".equals(type)) {
                OrderInfo orderQuery = new OrderInfo();
                orderQuery.setElderId(elderId);
                orderQuery.setOrderStatus("1"); // 已支付订单
                List<OrderInfo> orderList = orderInfoService.selectOrderInfoList(orderQuery);

                for (OrderInfo order : orderList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", "ORDER_" + order.getOrderId());
                    item.put("type", "expense");
                    item.put("title", "服务费扣款");
                    item.put("amount", order.getOrderAmount());
                    item.put("time", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, order.getCreateTime()));
                    item.put("description", getOrderTypeText(order.getOrderType()) + "订单支付");
                    item.put("canRefund", false);
                    expenseList.add(item);
                }
            }

            // 3. 查询其他费用记录（可扩展）
            if ("all".equals(type) || "other".equals(type)) {
                // 暂时没有其他费用记录的数据源
                // TODO: 可以根据业务需要添加其他费用类型
            }

            // 按时间倒序排序
            expenseList.sort((a, b) -> {
                String timeA = (String) a.get("time");
                String timeB = (String) b.get("time");
                return timeB.compareTo(timeA);
            });

            // 分页处理
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = startIndex + pageSize;
            List<Map<String, Object>> paginatedList = new java.util.ArrayList<>();

            for (int i = startIndex; i < endIndex && i < expenseList.size(); i++) {
                paginatedList.add(expenseList.get(i));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("rows", paginatedList);
            result.put("total", expenseList.size());
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            return success(result);
        } catch (Exception e) {
            logger.error("获取费用明细失败", e);
            return error("获取费用明细失败：" + e.getMessage());
        }
    }

    /**
     * 处理支付请求
     * 模拟支付接口，默认支付成功
     */
    @PostMapping("/payment/process/{orderId}")
    public AjaxResult processPayment(@PathVariable Long orderId) {
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

            // 验证订单权限 - 只能支付自己创建的订单
            if (order.getCreatorUserId() == null) {
                return error("订单数据异常，缺少创建者信息");
            }
            if (!currentUserId.equals(order.getCreatorUserId())) {
                return error("无权操作该订单");
            }

            // 检查订单状态
            if ("1".equals(order.getOrderStatus())) {
                return error("订单已支付");
            }
            if ("2".equals(order.getOrderStatus())) {
                return error("订单已取消");
            }

            // 模拟支付处理
            try {
                // 模拟支付接口调用延迟
                Thread.sleep(1000);

                // 支付成功，更新订单状态
                order.setOrderStatus("1"); // 1-已支付
                order.setPaymentTime(new Date());
                order.setPaidAmount(order.getOrderAmount());
                orderInfoService.updateOrderInfo(order);

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

            // 根据订单类型分配金额到不同余额类型
            BigDecimal serviceAmount = BigDecimal.ZERO;
            BigDecimal depositAmount = BigDecimal.ZERO;
            BigDecimal memberAmount = BigDecimal.ZERO;

            String orderType = order.getOrderType();
            if ("1".equals(orderType)) { // 入驻订单
                // 入驻订单通常包含：押金 + 会员费 + 服务费
                depositAmount = totalAmount.multiply(new BigDecimal("0.4")); // 40%押金
                memberAmount = totalAmount.multiply(new BigDecimal("0.1"));  // 10%会员费
                serviceAmount = totalAmount.subtract(depositAmount).subtract(memberAmount); // 剩余为服务费
            } else if ("2".equals(orderType)) { // 续费订单
                // 续费订单主要是服务费
                serviceAmount = totalAmount.multiply(new BigDecimal("0.9")); // 90%服务费
                memberAmount = totalAmount.multiply(new BigDecimal("0.1"));  // 10%其他费用
            } else { // 其他类型
                serviceAmount = totalAmount.multiply(new BigDecimal("0.8")); // 80%服务费
                depositAmount = totalAmount.multiply(new BigDecimal("0.2")); // 20%其他
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

        } catch (Exception e) {
            logger.error("更新账户余额失败", e);
            throw new RuntimeException("更新账户余额失败：" + e.getMessage());
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
            default:
                return "未知";
        }
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
}