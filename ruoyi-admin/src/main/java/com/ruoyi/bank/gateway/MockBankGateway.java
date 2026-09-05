package com.ruoyi.bank.gateway;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "bank.integration", name = "mode", havingValue = "mock")
public class MockBankGateway implements BankGateway
{
    private final Map<String, BankResult> payoutResults = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> availableBalances = new ConcurrentHashMap<>();

    @Override
    public BankResult createPayment(BankPaymentRequest request)
    {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            return BankResult.failed("MOCK_AMOUNT_INVALID", "支付金额必须大于0");
        }
        return BankResult.pending("MOCK-" + request.getRequestNo(),
                "mock-bank://checkout/" + request.getRequestNo());
    }

    @Override
    public BankResult queryPayment(BankQueryRequest request)
    {
        return BankResult.pending(request.getBankSerialNo(), null);
    }

    @Override
    public BankResult verifyMerchant(String merId, String settlementAccountNo)
    {
        if (merId == null || settlementAccountNo == null)
        {
            return BankResult.failed("MOCK_MERCHANT_INVALID", "商户号或结算账户为空");
        }
        return BankResult.success("MOCK-VERIFY-" + merId);
    }

    @Override
    public boolean supportsPayout()
    {
        return true;
    }

    @Override
    public BankResult submitPayout(BankPayoutRequest request)
    {
        request.validate();
        BankResult pending = BankResult.pending(null, null);
        pending.setRequestNo(request.getRequestNo());
        pending.setResponseMessage("模拟银行已受理，尚未确认拨付结果");
        pending.setPaidAmount(request.getAmount());
        pending.setPayerAccountNo(request.getPayerAccountNo());
        pending.setPayeeAccountNo(request.getPayeeAccountNo());
        BankResult existing = payoutResults.putIfAbsent(request.getRequestNo(), pending);
        return existing == null ? pending : existing;
    }

    @Override
    public BankResult queryPayout(BankPayoutRequest request)
    {
        request.validateQuery();
        BankResult result = payoutResults.get(request.getRequestNo());
        if (result == null)
        {
            return BankResult.unknown("MOCK_NOT_FOUND", "模拟银行未找到原请求，不能据此判定失败");
        }
        if (result.getRequestNo() == null || result.getRequestNo().trim().isEmpty())
        {
            result.setRequestNo(request.getRequestNo());
        }
        return result;
    }

    @Override
    public BankResult queryBalance(String accountNo, String accountName)
    {
        BigDecimal balance = availableBalances.get(accountNo);
        if (balance == null)
        {
            return BankResult.unknown("MOCK_BALANCE_UNSET", "尚未设置模拟银行可用余额");
        }
        BankResult result = BankResult.success(null);
        result.setPayerAccountNo(accountNo);
        result.setAvailableBalance(balance);
        return result;
    }

    /** 仅供 mock 环境的测试驱动，不提供 HTTP 修改入口，也不自动将受理当作成功。 */
    public void setPayoutResult(String requestNo, BankResult result)
    {
        payoutResults.put(requestNo, result);
    }

    public void setAvailableBalance(String accountNo, BigDecimal balance)
    {
        availableBalances.put(accountNo, balance);
    }
}
