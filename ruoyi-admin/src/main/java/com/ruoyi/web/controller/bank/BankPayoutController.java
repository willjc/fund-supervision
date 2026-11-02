package com.ruoyi.web.controller.bank;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.bank.gateway.BankAccountDetail;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.service.IPensionInstitutionService;
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
    @Autowired private IPensionInstitutionService institutions;
    @Autowired private BankSettlementMapper settlement;
    @Autowired private BankGateway gateway;

    /** 拨付流程总览：账户、商户、签约、平台闸门与待办汇总。账户号只返回掩码。 */
    @GetMapping("/flow/{institutionId}")
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    public AjaxResult flow(@PathVariable Long institutionId)
    {
        payouts.checkScope(institutionId);
        PensionInstitution institution = institutions.selectPensionInstitutionByInstitutionId(institutionId);
        if (institution == null) { throw new ServiceException("机构不存在"); }

        BankMerchantConfig query = new BankMerchantConfig();
        query.setInstitutionId(institutionId);
        BankMerchantConfig merchant = selectCurrent(merchants.selectList(query));

        Map<String, Object> flow = new HashMap<>();
        Map<String, Object> account = new LinkedHashMap<>();
        account.put("institutionId", institution.getInstitutionId());
        account.put("institutionName", institution.getInstitutionName());
        account.put("superviseAccount", mask(institution.getSuperviseAccount()));
        account.put("superviseBank", institution.getSuperviseBank());
        account.put("bankAccount", mask(institution.getBankAccount()));
        account.put("basicBank", institution.getBasicBank());
        account.put("accountConfigured", notBlank(institution.getSuperviseAccount()) && notBlank(institution.getBankAccount()));
        flow.put("institution", account);

        Map<String, Object> binding = new LinkedHashMap<>();
        binding.put("configured", merchant != null);
        if (merchant != null)
        {
            binding.put("configId", merchant.getConfigId());
            binding.put("merId", merchant.getMerId());
            binding.put("merchantName", merchant.getMerchantName());
            binding.put("environment", merchant.getEnvironment());
            binding.put("settlementAccountNo", mask(merchant.getSettlementAccountNo()));
            binding.put("settlementAccountName", merchant.getSettlementAccountName());
            binding.put("basicAccountNo", mask(merchant.getBasicAccountNo()));
            binding.put("basicAccountName", merchant.getBasicAccountName());
            binding.put("verifyStatus", merchant.getVerifyStatus());
            binding.put("verifyMessage", merchant.getVerifyMessage());
            binding.put("status", merchant.getStatus());
            binding.put("isDefault", merchant.getIsDefault());
            binding.put("payoutEnabled", merchant.getPayoutEnabled());
            binding.put("agreementNo", mask(merchant.getSupervisionAgreementNo()));
            binding.put("agreementSigned", notBlank(merchant.getSupervisionAgreementNo()));
            binding.put("lastVerifiedTime", merchant.getLastVerifiedTime());
        }
        flow.put("merchant", binding);

        Map<String, Object> gates = new LinkedHashMap<>();
        gates.put("platformSwitch", payouts.isSwitchEnabled());
        gates.put("protocolConfirmed", payouts.isProtocolConfirmed());
        gates.put("payoutEnabled", payouts.isEnabled());
        gates.put("merchantReady", merchant != null && "1".equals(merchant.getStatus())
                && "1".equals(merchant.getVerifyStatus()) && "1".equals(merchant.getIsDefault()));
        gates.put("merchantPayoutEnabled", merchant != null && Integer.valueOf(1).equals(merchant.getPayoutEnabled()));
        gates.put("agreementSigned", merchant != null && notBlank(merchant.getSupervisionAgreementNo()));
        gates.put("canSubmit", payouts.isEnabled() && merchant != null
                && "1".equals(merchant.getStatus()) && "1".equals(merchant.getVerifyStatus())
                && "1".equals(merchant.getIsDefault())
                && Integer.valueOf(1).equals(merchant.getPayoutEnabled())
                && notBlank(merchant.getSupervisionAgreementNo()));
        flow.put("gates", gates);

        flow.put("transactions", settlement.transactionSummary(institutionId));
        flow.put("transfers", settlement.transferSummary(institutionId));
        return AjaxResult.success(flow);
    }

    private BankMerchantConfig selectCurrent(List<BankMerchantConfig> configs)
    {
        BankMerchantConfig selected = null;
        for (BankMerchantConfig config : configs)
        {
            if (selected == null || score(config) > score(selected)
                    || (score(config) == score(selected)
                    && config.getConfigId() != null && selected.getConfigId() != null
                    && config.getConfigId() > selected.getConfigId()))
            { selected = config; }
        }
        return selected;
    }

    private int score(BankMerchantConfig config)
    {
        return ("1".equals(config.getStatus()) ? 4 : 0)
                + ("1".equals(config.getVerifyStatus()) ? 2 : 0)
                + ("1".equals(config.getIsDefault()) ? 1 : 0);
    }

    private boolean notBlank(String value) { return value != null && !value.trim().isEmpty(); }

    private String mask(String value)
    {
        if (value == null || value.trim().isEmpty()) { return null; }
        String text = value.trim();
        return text.length() <= 8 ? "****" : text.substring(0, 4) + "****" + text.substring(text.length() - 4);
    }

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
        result.put("returnTime", tx == null ? null : tx.getReturnTime());
        result.put("returnReason", tx == null ? null : tx.getReturnReason());
        return AjaxResult.success(result);
    }

    @PostMapping("/{transferId}/submit")
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:execute')")
    @Log(title = "银行拨付立即发送", businessType = BusinessType.UPDATE)
    public AjaxResult submit(@PathVariable Long transferId)
    {
        scoped(transferId);
        BankTransaction tx = payouts.submit(transferId, SecurityUtils.getUsername(), false);
        return AjaxResult.success("已向银行发起拨付请求，请查询最终结果", tx.getRequestNo());
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

    /** 监管账户交易明细（OBK0071708）：stardt/enddat 为 yyyyMMdd，pagenm/rcrdnm 可选（银行默认 1/10）。 */
    @GetMapping("/detail/{institutionId}")
    @PreAuthorize("@ss.hasPermi('pension:bank:detail')")
    public AjaxResult detail(@PathVariable Long institutionId,
            @RequestParam String stardt, @RequestParam String enddat,
            @RequestParam(required = false) String pagenm, @RequestParam(required = false) String rcrdnm)
    {
        payouts.checkScope(institutionId);
        if (stardt == null || !stardt.matches("\\d{8}") || enddat == null || !enddat.matches("\\d{8}"))
        {
            throw new ServiceException("查询日期格式为 yyyyMMdd");
        }
        BankMerchantConfig config = merchants.selectEnabledByInstitutionId(institutionId);
        if (config == null) { throw new ServiceException("未配置有效银行绑定"); }
        List<BankAccountDetail> rows = gateway.queryAccountDetails(
                config.getSettlementAccountNo(), stardt, enddat, pagenm, rcrdnm);
        Map<String, Object> result = new HashMap<>();
        result.put("rows", rows);
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
