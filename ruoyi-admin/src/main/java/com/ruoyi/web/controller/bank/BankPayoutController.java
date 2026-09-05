package com.ruoyi.web.controller.bank;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.bank.impl.BankPayoutService;

@RestController
@RequestMapping("/pension/bank/payout")
public class BankPayoutController
{
    @Autowired private BankPayoutService payouts;
    @Autowired private FundTransferMapper transfers;
    @Autowired private BankTransactionMapper transactions;
    @Autowired private IBankMerchantConfigService merchants;
    @Autowired private BankGateway gateway;

    @PostMapping("/{transferId}/query")
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @Log(title = "银行拨付结果查询", businessType = BusinessType.UPDATE)
    public AjaxResult query(@PathVariable Long transferId)
    {
        FundTransfer transfer = scoped(transferId);
        BankTransaction tx = transactions.selectByBusiness("TRANSFER", transferId);
        if (tx != null) { tx = payouts.reconcile(tx.getRequestNo()); }
        Map<String, Object> result = new HashMap<>();
        result.put("transfer", transfers.selectFundTransferByTransferId(transfer.getTransferId()));
        // 不将账户快照/完整银行请求暴露给浏览器。
        result.put("bankStatus", tx == null ? null : tx.getBankStatus());
        result.put("bookingStatus", tx == null ? null : tx.getBookingStatus());
        result.put("manualReview", tx == null ? 0 : tx.getManualReview());
        result.put("requestNo", tx == null ? null : tx.getRequestNo());
        result.put("bankTime", tx == null ? null : tx.getBankTime());
        return AjaxResult.success(result);
    }

    @PostMapping("/{transferId}/retry")
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:execute')")
    @Log(title = "银行拨付失败重试", businessType = BusinessType.UPDATE)
    public AjaxResult retry(@PathVariable Long transferId)
    {
        scoped(transferId);
        BankTransaction tx = payouts.submit(transferId, SecurityUtils.getUsername(), true);
        return AjaxResult.success("已提交，请查询银行最终结果", tx.getRequestNo());
    }

    @GetMapping("/balance/{institutionId}")
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    public AjaxResult balance(@PathVariable Long institutionId)
    {
        payouts.checkScope(institutionId);
        BankMerchantConfig config = merchants.selectEnabledByInstitutionId(institutionId);
        if (config == null) { throw new ServiceException("未配置有效银行绑定"); }
        BankResult bank = gateway.queryBalance(config.getSettlementAccountNo(), config.getSettlementAccountName());
        Map<String, Object> result = new HashMap<>();
        result.put("status", bank.getStatus());
        result.put("availableBalance", bank.getAvailableBalance());
        result.put("queriedAt", new Date());
        return AjaxResult.success(result);
    }

    private FundTransfer scoped(Long id)
    {
        FundTransfer transfer = transfers.selectFundTransferByTransferId(id);
        if (transfer == null) { throw new ServiceException("拨付单不存在"); }
        payouts.checkScope(transfer.getInstitutionId());
        return transfer;
    }
}
