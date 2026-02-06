package com.ruoyi.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.service.IOrderInfoService;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 订单主表Controller
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/order/info")
public class OrderInfoController extends BaseController
{
    @Autowired
    private IOrderInfoService orderInfoService;

    @Autowired
    private IBedInfoService bedInfoService;

    @Autowired
    private BedAllocationMapper bedAllocationMapper;

    /**
     * 查询订单主表列表
     */
    @PreAuthorize("@ss.hasPermi('order:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(OrderInfo orderInfo)
    {
        startPage();
        // 数据权限过滤: admin超级管理员可以看到所有订单，其他用户只能看到关联机构的订单
        if (!getUserId().equals(1L)) {
            orderInfo.setCurrentUserId(getUserId());
        }
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        return getDataTable(list);
    }

    /**
     * 导出订单主表列表
     */
    @PreAuthorize("@ss.hasPermi('order:info:export')")
    @Log(title = "订单主表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderInfo orderInfo)
    {
        List<OrderInfo> list = orderInfoService.exportOrderInfo(orderInfo);
        ExcelUtil<OrderInfo> util = new ExcelUtil<OrderInfo>(OrderInfo.class);
        util.exportExcel(response, list, "订单主表数据");
    }

    /**
     * 获取订单主表详细信息
     */
    @PreAuthorize("@ss.hasPermi('order:info:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return AjaxResult.success(orderInfoService.selectOrderInfoByOrderId(orderId));
    }

    /**
     * 根据订单编号查询订单
     */
    @PreAuthorize("@ss.hasPermi('order:info:query')")
    @GetMapping(value = "/orderNo/{orderNo}")
    public AjaxResult getByOrderNo(@PathVariable("orderNo") String orderNo)
    {
        return AjaxResult.success(orderInfoService.selectOrderInfoByOrderNo(orderNo));
    }

    /**
     * 新增订单主表
     */
    @PreAuthorize("@ss.hasPermi('order:info:add')")
    @Log(title = "订单主表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderInfo orderInfo)
    {
        return toAjax(orderInfoService.insertOrderInfo(orderInfo));
    }

    /**
     * 修改订单主表
     */
    @PreAuthorize("@ss.hasPermi('order:info:edit')")
    @Log(title = "订单主表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderInfo orderInfo)
    {
        return toAjax(orderInfoService.updateOrderInfo(orderInfo));
    }

    /**
     * 处理订单支付
     */
    @PreAuthorize("@ss.hasPermi('order:info:pay')")
    @Log(title = "订单支付", businessType = BusinessType.UPDATE)
    @PutMapping("/pay/{orderId}")
    public AjaxResult pay(@PathVariable("orderId") Long orderId, @RequestBody OrderInfo orderInfo)
    {
        return toAjax(orderInfoService.processOrderPayment(orderId, orderInfo.getPaymentMethod()));
    }

    /**
     * 取消订单
     */
    @PreAuthorize("@ss.hasPermi('order:info:cancel')")
    @Log(title = "取消订单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable("orderId") Long orderId, @RequestBody OrderInfo orderInfo)
    {
        return toAjax(orderInfoService.cancelOrder(orderId, orderInfo.getRemark()));
    }

    /**
     * 根据入住申请生成订单
     */
    @PreAuthorize("@ss.hasPermi('order:info:generate')")
    @Log(title = "生成订单", businessType = BusinessType.INSERT)
    @PostMapping("/generate/{checkInId}")
    public AjaxResult generateOrders(@PathVariable("checkInId") Long checkInId)
    {
        return toAjax(orderInfoService.generateOrdersByCheckIn(checkInId));
    }

    /**
     * 审核通过订单
     * 管理员审核订单，可同时修改订单信息和更换床位
     */
    @PreAuthorize("@ss.hasPermi('order:info:audit')")
    @Log(title = "审核订单", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody OrderInfo orderInfo)
    {
        // 获取原订单信息
        OrderInfo oldOrder = orderInfoService.selectOrderInfoByOrderId(orderInfo.getOrderId());

        // 如果更换了床位，需要更新床位状态
        Long newBedId = orderInfo.getBedId();
        if (newBedId != null && !newBedId.equals(oldOrder.getBedId())) {
            // 原床位恢复为空置
            if (oldOrder.getBedId() != null) {
                bedInfoService.updateBedStatus(oldOrder.getBedId(), "0");
            }
            // 新床位改为占用
            bedInfoService.updateBedStatus(newBedId, "1");

            // 同步更新 bed_allocation 表的床位信息，确保管理端入住人列表显示正确
            bedAllocationMapper.updateBedIdByElderIdAndInstitutionId(
                oldOrder.getElderId(),
                newBedId,
                oldOrder.getInstitutionId()
            );
        }

        orderInfo.setOrderStatus("5"); // 5-审核通过
        return toAjax(orderInfoService.updateOrderInfo(orderInfo));
    }

    /**
     * 审核拒绝订单
     */
    @PreAuthorize("@ss.hasPermi('order:info:audit')")
    @Log(title = "审核拒绝订单", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody OrderInfo orderInfo)
    {
        orderInfo.setOrderStatus("2"); // 2-已取消（审核拒绝）
        return toAjax(orderInfoService.updateOrderInfo(orderInfo));
    }

    /**
     * 线下支付订单
     */
    @PreAuthorize("@ss.hasPermi('order:info:offlinePay')")
    @Log(title = "线下支付订单", businessType = BusinessType.UPDATE)
    @PutMapping("/offlinePay/{orderId}")
    public AjaxResult offlinePay(@PathVariable("orderId") Long orderId, @RequestBody OrderInfo orderInfo)
    {
        return toAjax(orderInfoService.offlinePayOrder(orderId, orderInfo.getPaymentMethod(),
            orderInfo.getPaymentProof(), orderInfo.getPaymentProofRemark()));
    }

    /**
     * 删除订单主表
     */
    @PreAuthorize("@ss.hasPermi('order:info:remove')")
    @Log(title = "订单主表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(orderInfoService.deleteOrderInfoByOrderIds(orderIds));
    }
}