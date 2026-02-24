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
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.service.IPaymentRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import java.math.BigDecimal;

/**
 * 支付记录Controller
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/payment/record")
public class PaymentRecordController extends BaseController
{
    @Autowired
    private IPaymentRecordService paymentRecordService;

    /**
     * 查询支付记录列表
     */
    @PreAuthorize("@ss.hasPermi('payment:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(PaymentRecord paymentRecord)
    {
        startPage();
        List<PaymentRecord> list = paymentRecordService.selectPaymentRecordList(paymentRecord);
        return getDataTable(list);
    }

    /**
     * 导出支付记录列表
     */
    @PreAuthorize("@ss.hasPermi('payment:record:export')")
    @Log(title = "支付记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PaymentRecord paymentRecord)
    {
        List<PaymentRecord> list = paymentRecordService.exportPaymentRecord(paymentRecord);
        ExcelUtil<PaymentRecord> util = new ExcelUtil<PaymentRecord>(PaymentRecord.class);
        util.exportExcel(response, list, "支付记录数据");
    }

    /**
     * 获取支付记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('payment:record:query')")
    @GetMapping(value = "/{paymentId}")
    public AjaxResult getInfo(@PathVariable("paymentId") Long paymentId)
    {
        return AjaxResult.success(paymentRecordService.selectPaymentRecordByPaymentId(paymentId));
    }

    /**
     * 根据支付流水号查询支付记录
     */
    @PreAuthorize("@ss.hasPermi('payment:record:query')")
    @GetMapping(value = "/paymentNo/{paymentNo}")
    public AjaxResult getByPaymentNo(@PathVariable("paymentNo") String paymentNo)
    {
        return AjaxResult.success(paymentRecordService.selectPaymentRecordByPaymentNo(paymentNo));
    }

    /**
     * 新增支付记录
     */
    @PreAuthorize("@ss.hasPermi('payment:record:add')")
    @Log(title = "支付记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PaymentRecord paymentRecord)
    {
        return toAjax(paymentRecordService.insertPaymentRecord(paymentRecord));
    }

    /**
     * 修改支付记录
     */
    @PreAuthorize("@ss.hasPermi('payment:record:edit')")
    @Log(title = "支付记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PaymentRecord paymentRecord)
    {
        return toAjax(paymentRecordService.updatePaymentRecord(paymentRecord));
    }

    /**
     * 更新支付状态
     */
    @PreAuthorize("@ss.hasPermi('payment:record:status')")
    @Log(title = "更新支付状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{paymentId}")
    public AjaxResult updateStatus(@PathVariable("paymentId") Long paymentId, @RequestBody PaymentRecord paymentRecord)
    {
        return toAjax(paymentRecordService.updatePaymentStatus(paymentId, paymentRecord.getPaymentStatus()));
    }

    /**
     * 模拟支付处理
     */
    @PreAuthorize("@ss.hasPermi('order:info:pay')")
    @Log(title = "模拟支付", businessType = BusinessType.UPDATE)
    @PostMapping("/simulate/{orderId}")
    public AjaxResult simulatePayment(@PathVariable("orderId") Long orderId, @RequestBody PaymentRecord paymentRecord)
    {
        PaymentRecord result = paymentRecordService.simulatePayment(orderId, paymentRecord.getPaymentMethod());
        if (result != null) {
            return AjaxResult.success("支付成功", result);
        } else {
            return AjaxResult.error("支付失败，请检查订单信息");
        }
    }

    /**
     * 获取支付统计信息
     */
    @PreAuthorize("@ss.hasPermi('payment:record:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics(PaymentRecord paymentRecord)
    {
        BigDecimal totalAmount = paymentRecordService.getPaymentAmountTotal(paymentRecord);
        return AjaxResult.success()
                .put("totalAmount", totalAmount)
                .put("totalCount", paymentRecordService.selectPaymentRecordList(paymentRecord).size());
    }

    /**
     * 删除支付记录
     */
    @PreAuthorize("@ss.hasPermi('payment:record:remove')")
    @Log(title = "支付记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{paymentIds}")
    public AjaxResult remove(@PathVariable Long[] paymentIds)
    {
        return toAjax(paymentRecordService.deletePaymentRecordByPaymentIds(paymentIds));
    }
}