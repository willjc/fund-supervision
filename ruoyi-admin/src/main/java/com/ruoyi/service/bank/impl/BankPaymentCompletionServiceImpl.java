package com.ruoyi.service.bank.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.BedAllocation;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.domain.bank.BankPaymentCompletionResult;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.domain.pension.SupervisionAccountLog;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.mapper.PaymentRecordMapper;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.service.bank.IBankPaymentCompletionService;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.IFundTransferService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

@Service
public class BankPaymentCompletionServiceImpl implements IBankPaymentCompletionService
{
    private static final String BUSINESS_TYPE_PAY = "PAY";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";

    @Autowired private BankTransactionMapper transactionMapper;
    @Autowired private OrderInfoMapper orderInfoMapper;
    @Autowired private PaymentRecordMapper paymentRecordMapper;
    @Autowired private PensionInstitutionMapper institutionMapper;
    @Autowired private ElderInfoMapper elderInfoMapper;
    @Autowired private AccountInfoMapper accountInfoMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private IExpenseRecordService expenseRecordService;
    @Autowired private ISupervisionAccountLogService supervisionAccountLogService;
    @Autowired private IFundTransferService fundTransferService;
    @Autowired private BedAllocationMapper bedAllocationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankPaymentCompletionResult completePayment(String requestNo, String bankSerialNo,
            String responseCode, String responseMessage, String operator)
    {
        if (!StringUtils.hasText(requestNo) || !StringUtils.hasText(bankSerialNo))
        {
            throw new ServiceException("银行请求流水号和银行流水号不能为空");
        }

        BankTransaction transaction = transactionMapper.selectByRequestNoForUpdate(requestNo);
        if (transaction == null)
        {
            throw new ServiceException("银行交易不存在");
        }
        if (!BUSINESS_TYPE_PAY.equals(transaction.getBusinessType()))
        {
            throw new ServiceException("该银行交易不是支付交易");
        }
        if (transaction.getBusinessId() == null)
        {
            throw new ServiceException("银行交易未关联订单");
        }

        OrderInfo order = orderInfoMapper.selectOrderInfoByOrderIdForUpdate(transaction.getBusinessId());
        validateTransactionAndOrder(transaction, order);

        if (STATUS_SUCCESS.equals(transaction.getStatus()))
        {
            return validateAlreadyCompleted(transaction, order, bankSerialNo);
        }
        if (STATUS_FAILED.equals(transaction.getStatus()))
        {
            throw new ServiceException("失败状态的银行交易不能完成支付");
        }
        if (!STATUS_PENDING.equals(transaction.getStatus()))
        {
            throw new ServiceException("当前银行交易状态不能完成支付：" + transaction.getStatus());
        }
        if (!"0".equals(order.getOrderStatus()) && !"5".equals(order.getOrderStatus()))
        {
            throw new ServiceException("订单状态不是待支付，不能完成银行入账");
        }

        String actualOperator = StringUtils.hasText(operator) ? operator : "system";
        String paymentMethod = toPaymentMethod(transaction.getChannelType());
        Date paymentTime = new Date();

        // 锁定机构以串行化同一监管账户的余额流水；锁定老人以串行化账户创建。
        if (institutionMapper.selectPensionInstitutionForUpdate(order.getInstitutionId()) == null)
        {
            throw new ServiceException("订单关联的养老机构不存在");
        }
        if (order.getElderId() == null)
        {
            throw new ServiceException("订单未关联老人");
        }
        ElderInfo elder = elderInfoMapper.selectElderInfoForUpdate(order.getElderId());
        if (elder == null)
        {
            throw new ServiceException("订单关联的老人不存在");
        }

        List<OrderItem> items = orderItemMapper.selectOrderItemsByOrderId(order.getOrderId());
        Allocation allocation = allocate(order, items);
        BigDecimal firstMonthFee = "1".equals(order.getOrderType())
                ? calculateFirstMonthFee(items) : BigDecimal.ZERO;
        if (firstMonthFee.compareTo(allocation.serviceAmount) > 0)
        {
            throw new ServiceException("首月服务费超过订单服务费金额");
        }

        AccountInfo account = lockOrCreateAccount(order, elder, requestNo);
        BigDecimal balanceBefore = value(account.getTotalBalance());
        BigDecimal serviceBefore = value(account.getServiceBalance());
        BigDecimal depositBefore = value(account.getDepositBalance());
        BigDecimal memberBefore = value(account.getMemberBalance());
        BigDecimal balanceAfterIncome = balanceBefore.add(order.getOrderAmount());

        if (orderInfoMapper.markOrderPaid(order.getOrderId(), order.getOrderAmount(),
                paymentMethod, paymentTime, actualOperator) != 1)
        {
            throw new ServiceException("更新订单支付状态失败");
        }

        insertPaymentRecord(transaction, order, bankSerialNo, responseCode,
                responseMessage, paymentMethod, paymentTime, actualOperator);

        BigDecimal finalTotal = balanceAfterIncome.subtract(firstMonthFee);
        BigDecimal finalService = serviceBefore.add(allocation.serviceAmount).subtract(firstMonthFee);
        BigDecimal finalDeposit = depositBefore.add(allocation.depositAmount);
        BigDecimal finalMember = memberBefore.add(allocation.memberAmount);
        if (accountInfoMapper.updateAccountBalance(account.getAccountId(), finalTotal,
                finalService, finalDeposit, finalMember) != 1)
        {
            throw new ServiceException("更新老人账户余额失败");
        }

        int expectedExpenseCount = positiveCount(allocation.depositAmount,
                allocation.serviceAmount, allocation.memberAmount, allocation.otherAmount)
                + (firstMonthFee.signum() > 0 ? 1 : 0);
        int expenseCount = expenseRecordService.createOrderExpenseRecords(
                order.getElderId(), account.getAccountId(), order.getOrderId(), order.getOrderType(),
                allocation.depositAmount, allocation.serviceAmount, allocation.memberAmount,
                allocation.otherAmount, balanceBefore, balanceAfterIncome, firstMonthFee);
        if (expenseCount != expectedExpenseCount)
        {
            throw new ServiceException("创建订单费用记录不完整");
        }

        SupervisionAccountLog incomeLog = supervisionAccountLogService.recordIncome(
                order.getInstitutionId(), order.getOrderId(), order.getOrderAmount(),
                "用户支付订单-" + order.getOrderNo(), actualOperator);
        requirePersistedLog(incomeLog, "记录监管账户收入流水失败");

        handleOrderFollowUp(order, items, firstMonthFee);

        Date completeTime = new Date();
        if (transactionMapper.markSuccess(transaction.getTransactionId(), bankSerialNo,
                bounded(responseCode, 64), bounded(responseMessage, 500), completeTime, completeTime) != 1)
        {
            throw new ServiceException("更新银行交易完成状态失败");
        }

        return BankPaymentCompletionResult.completed(order.getOrderId(), order.getOrderNo(),
                order.getOrderAmount(), paymentTime, false);
    }

    private void validateTransactionAndOrder(BankTransaction transaction, OrderInfo order)
    {
        if (order == null)
        {
            throw new ServiceException("银行交易关联的订单不存在");
        }
        if (!transaction.getBusinessId().equals(order.getOrderId()))
        {
            throw new ServiceException("银行交易关联订单不一致");
        }
        if (transaction.getInstitutionId() == null
                || !transaction.getInstitutionId().equals(order.getInstitutionId()))
        {
            throw new ServiceException("银行交易机构与订单机构不一致");
        }
        if (transaction.getAmount() == null || order.getOrderAmount() == null
                || transaction.getAmount().compareTo(order.getOrderAmount()) != 0)
        {
            throw new ServiceException("银行交易金额与订单金额不一致");
        }
    }

    private BankPaymentCompletionResult validateAlreadyCompleted(BankTransaction transaction,
            OrderInfo order, String bankSerialNo)
    {
        if (!"1".equals(order.getOrderStatus())
                || order.getPaidAmount() == null
                || order.getPaidAmount().compareTo(transaction.getAmount()) != 0
                || order.getPaymentTime() == null)
        {
            throw new ServiceException("银行交易已成功，但订单支付状态不一致");
        }
        if (!bankSerialNo.equals(transaction.getBankSerialNo()))
        {
            throw new ServiceException("重复通知的银行流水号不一致");
        }

        PaymentRecord payment = paymentRecordMapper.selectPaymentRecordByPaymentNo(paymentNo(transaction.getRequestNo()));
        if (payment == null || !"1".equals(payment.getPaymentStatus())
                || !order.getOrderId().equals(payment.getOrderId())
                || !order.getInstitutionId().equals(payment.getInstitutionId())
                || payment.getPaymentAmount() == null
                || payment.getPaymentAmount().compareTo(transaction.getAmount()) != 0
                || !bankSerialNo.equals(payment.getTransactionId()))
        {
            throw new ServiceException("银行交易已成功，但支付记录不一致");
        }

        return BankPaymentCompletionResult.completed(order.getOrderId(), order.getOrderNo(),
                order.getPaidAmount(), order.getPaymentTime(), true);
    }

    private AccountInfo lockOrCreateAccount(OrderInfo order, ElderInfo elder, String requestNo)
    {
        AccountInfo account = accountInfoMapper.selectAccountInfoForUpdate(
                order.getElderId(), order.getInstitutionId());
        if (account == null)
        {
            AccountInfo created = new AccountInfo();
            created.setElderId(order.getElderId());
            created.setInstitutionId(order.getInstitutionId());
            created.setAccountNo("ACC" + requestNo);
            created.setAccountName("账户-" + (StringUtils.hasText(elder.getElderName())
                    ? elder.getElderName() : order.getElderId()));
            created.setAccountStatus("1");
            created.setTotalBalance(BigDecimal.ZERO);
            created.setServiceBalance(BigDecimal.ZERO);
            created.setDepositBalance(BigDecimal.ZERO);
            created.setMemberBalance(BigDecimal.ZERO);
            created.setCreateBy("system");
            created.setCreateTime(new Date());
            created.setRemark("银行支付入账时自动创建");
            if (accountInfoMapper.insertAccountInfo(created) != 1)
            {
                throw new ServiceException("创建老人账户失败");
            }
            account = accountInfoMapper.selectAccountInfoForUpdate(
                    order.getElderId(), order.getInstitutionId());
        }
        if (account == null)
        {
            throw new ServiceException("锁定老人账户失败");
        }
        if (!"1".equals(account.getAccountStatus()))
        {
            throw new ServiceException("老人账户不是正常状态，不能入账");
        }
        return account;
    }

    private Allocation allocate(OrderInfo order, List<OrderItem> sourceItems)
    {
        List<OrderItem> items = sourceItems == null ? Collections.emptyList() : sourceItems;
        Allocation allocation = new Allocation();
        if (items.isEmpty())
        {
            allocation.serviceAmount = order.getOrderAmount();
            return allocation;
        }

        BigDecimal allocated = BigDecimal.ZERO;
        for (OrderItem item : items)
        {
            BigDecimal amount = item.getTotalAmount();
            if (amount == null || amount.signum() < 0)
            {
                throw new ServiceException("订单明细金额不合法");
            }
            allocated = allocated.add(amount);
            if ("deposit".equals(item.getItemType()))
            {
                allocation.depositAmount = allocation.depositAmount.add(amount);
            }
            else if ("member_fee".equals(item.getItemType()))
            {
                allocation.memberAmount = allocation.memberAmount.add(amount);
            }
            else
            {
                allocation.serviceAmount = allocation.serviceAmount.add(amount);
            }
        }
        if (allocated.compareTo(order.getOrderAmount()) != 0)
        {
            throw new ServiceException("订单明细合计与订单金额不一致");
        }
        return allocation;
    }

    private void insertPaymentRecord(BankTransaction transaction, OrderInfo order,
            String bankSerialNo, String responseCode, String responseMessage,
            String paymentMethod, Date paymentTime, String operator)
    {
        PaymentRecord payment = new PaymentRecord();
        payment.setPaymentNo(paymentNo(transaction.getRequestNo()));
        payment.setOrderId(order.getOrderId());
        payment.setOrderNo(order.getOrderNo());
        payment.setElderId(order.getElderId());
        payment.setInstitutionId(order.getInstitutionId());
        payment.setPaymentAmount(order.getOrderAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("1");
        payment.setPaymentTime(paymentTime);
        payment.setTransactionId(bankSerialNo);
        payment.setGatewayResponse(bounded(safe(responseCode) + ":" + safe(responseMessage), 500));
        payment.setOperator(operator);
        payment.setCreateBy(operator);
        payment.setCreateTime(paymentTime);
        if (paymentRecordMapper.insertPaymentRecord(payment) != 1)
        {
            throw new ServiceException("创建支付记录失败");
        }
    }

    private void handleOrderFollowUp(OrderInfo order, List<OrderItem> items, BigDecimal firstMonthFee)
    {
        if ("1".equals(order.getOrderType()))
        {
            if (firstMonthFee.signum() <= 0)
            {
                return;
            }
            FundTransfer transfer = createFirstMonthTransfer(order, firstMonthFee);
            SupervisionAccountLog outLog = supervisionAccountLogService.recordTransferOut(
                    order.getInstitutionId(), transfer.getTransferId(), firstMonthFee,
                    "首月服务费划拨-" + order.getOrderNo(), "基本账户");
            requirePersistedLog(outLog, "记录首月服务费划拨流水失败");

            int monthCount = order.getMonthCount() == null ? 1 : order.getMonthCount();
            if (monthCount > 1)
            {
                fundTransferService.generateMonthlyTransfersForOrder(
                        order.getOrderId(), order.getInstitutionId(), order.getElderId(),
                        monthCount - 1, defaultDate(order.getServiceStartDate()),
                        firstMonthFee, false);
            }
            return;
        }

        if ("2".equals(order.getOrderType()))
        {
            int monthCount = order.getMonthCount() == null ? 1 : order.getMonthCount();
            BedAllocation bedAllocation = bedAllocationMapper.selectBedAllocationByElderId(order.getElderId());
            if (bedAllocation != null && monthCount > 0 && order.getServiceEndDate() != null)
            {
                bedAllocation.setDueDate(order.getServiceEndDate());
                bedAllocation.setUpdateTime(new Date());
                if (bedAllocationMapper.updateBedAllocation(bedAllocation) != 1)
                {
                    throw new ServiceException("更新续费床位到期日失败");
                }
            }

            BigDecimal monthlyFee = calculateRenewMonthlyFee(items);
            if (monthCount > 0 && monthlyFee.signum() > 0)
            {
                fundTransferService.generateMonthlyTransfersForOrder(
                        order.getOrderId(), order.getInstitutionId(), order.getElderId(),
                        monthCount, defaultDate(order.getServiceStartDate()), monthlyFee, false);
            }
        }
    }

    private FundTransfer createFirstMonthTransfer(OrderInfo order, BigDecimal amount)
    {
        Date now = new Date();
        String period = new SimpleDateFormat("yyyy-MM").format(now);
        FundTransfer transfer = new FundTransfer();
        transfer.setInstitutionId(order.getInstitutionId());
        transfer.setElderId(order.getElderId());
        transfer.setOrderId(order.getOrderId());
        transfer.setTransferNo("TRF-BANK-" + order.getOrderId() + "-FM");
        transfer.setTransferType("1");
        transfer.setTransferAmount(amount);
        transfer.setTransferDate(now);
        transfer.setTransferPeriod(period);
        transfer.setBillingMonth(period);
        transfer.setElderCount(1);
        transfer.setTransferStatus("1");
        transfer.setIsPaid("1");
        transfer.setStatus("completed");
        transfer.setExecuteUser("system");
        transfer.setExecuteTime(now);
        transfer.setPaidTime(now);
        transfer.setPaidMethod("auto");
        transfer.setCreateBy("system");
        transfer.setCreateTime(now);
        transfer.setRemark("首月服务费立即划拨-" + order.getOrderNo());
        if (fundTransferService.insertFundTransfer(transfer) != 1 || transfer.getTransferId() == null)
        {
            throw new ServiceException("生成首月服务费拨付单失败");
        }
        return transfer;
    }

    private BigDecimal calculateFirstMonthFee(List<OrderItem> sourceItems)
    {
        BigDecimal fee = BigDecimal.ZERO;
        if (sourceItems == null)
        {
            return fee;
        }
        for (OrderItem item : sourceItems)
        {
            if (isMonthlyServiceItem(item.getItemType()))
            {
                long quantity = item.getQuantity() == null ? 1L : item.getQuantity();
                if (quantity <= 0)
                {
                    throw new ServiceException("订单服务明细数量不合法");
                }
                fee = fee.add(value(item.getTotalAmount()).divide(
                        BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP));
            }
        }
        return fee;
    }

    private BigDecimal calculateRenewMonthlyFee(List<OrderItem> sourceItems)
    {
        BigDecimal total = BigDecimal.ZERO;
        long monthCount = 1L;
        if (sourceItems == null)
        {
            return total;
        }
        for (OrderItem item : sourceItems)
        {
            if (isMonthlyServiceItem(item.getItemType()))
            {
                total = total.add(value(item.getTotalAmount()));
                if ("bed_fee".equals(item.getItemType()) && item.getQuantity() != null)
                {
                    monthCount = item.getQuantity();
                }
            }
        }
        if (monthCount <= 0)
        {
            throw new ServiceException("续费订单月份不合法");
        }
        return total.divide(BigDecimal.valueOf(monthCount), 2, RoundingMode.HALF_UP);
    }

    private boolean isMonthlyServiceItem(String itemType)
    {
        return "bed_fee".equals(itemType) || "care_fee".equals(itemType) || "meal_fee".equals(itemType);
    }

    private int positiveCount(BigDecimal... amounts)
    {
        int count = 0;
        for (BigDecimal amount : amounts)
        {
            if (amount != null && amount.signum() > 0)
            {
                count++;
            }
        }
        return count;
    }

    private void requirePersistedLog(SupervisionAccountLog log, String message)
    {
        if (log == null || log.getLogId() == null)
        {
            throw new ServiceException(message);
        }
    }

    private String toPaymentMethod(String channelType)
    {
        if ("wechat".equals(channelType)) return "微信";
        if ("alipay".equals(channelType)) return "支付宝";
        return StringUtils.hasText(channelType) ? channelType : "银行支付";
    }

    private static String paymentNo(String requestNo)
    {
        return "PAY" + DigestUtils.md5DigestAsHex(requestNo.getBytes(StandardCharsets.UTF_8));
    }

    private static BigDecimal value(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private static String safe(String value)
    {
        return value == null ? "" : value;
    }

    private static String bounded(String value, int maxLength)
    {
        String normalized = safe(value);
        return normalized.length() <= maxLength ? normalized : normalized.substring(0, maxLength);
    }

    private static Date defaultDate(Date date)
    {
        return date == null ? new Date() : date;
    }

    private static final class Allocation
    {
        private BigDecimal serviceAmount = BigDecimal.ZERO;
        private BigDecimal depositAmount = BigDecimal.ZERO;
        private BigDecimal memberAmount = BigDecimal.ZERO;
        private BigDecimal otherAmount = BigDecimal.ZERO;
    }
}
