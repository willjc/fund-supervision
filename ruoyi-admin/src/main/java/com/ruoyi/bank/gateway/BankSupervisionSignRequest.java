package com.ruoyi.bank.gateway;

import com.ruoyi.common.exception.ServiceException;

/**
 * OBK0071701（ylcntr 监管签约）请求参数。
 * jg* 为监管账户，hb* 为结算/回款账户；与 BankMerchantConfig 的 settlement=监管、basic=结算 命名相反，
 * 因此这里按银行字段语义命名，映射在服务层完成。
 */
public class BankSupervisionSignRequest
{
    /** jgacct 监管账户号，必输，≤40 */
    private String superviseAccountNo;
    /** jgacna 监管账户名称，必输，≤256 */
    private String superviseAccountName;
    /** hbskno 结算（回款）账户号，必输，≤40 */
    private String payeeAccountNo;
    /** hbskna 结算（回款）账户名称，必输，≤256 */
    private String payeeAccountName;
    /** 结算账户是否跨行（与 BankMerchantConfig.crossBank 一致）：1 跨行对应 banktg=2 行外，否则 banktg=1 行内 */
    private Integer crossBank;
    /** hbbank 结算账户开户行跨行联行号，选输，≤20 */
    private String payeeBankNo;
    /** socode 统一社会信用代码，选输，≤40 */
    private String unifiedCreditCode;

    public void validate()
    {
        requireField(superviseAccountNo, "监管账户号不能为空", 40);
        requireField(superviseAccountName, "监管账户名称不能为空", 256);
        requireField(payeeAccountNo, "结算账户号不能为空", 40);
        requireField(payeeAccountName, "结算账户名称不能为空", 256);
        if (payeeBankNo != null && payeeBankNo.length() > 20)
        {
            throw new ServiceException("跨行联行号长度不能超过20位");
        }
        if (unifiedCreditCode != null && unifiedCreditCode.length() > 40)
        {
            throw new ServiceException("统一社会信用代码长度不能超过40位");
        }
    }

    private static void requireField(String value, String message, int maxLength)
    {
        if (value == null || value.trim().isEmpty())
        {
            throw new ServiceException(message);
        }
        if (value.length() > maxLength)
        {
            throw new ServiceException(message.replace("不能为空", "长度不能超过" + maxLength + "位"));
        }
    }

    public String getSuperviseAccountNo() { return superviseAccountNo; }
    public void setSuperviseAccountNo(String superviseAccountNo) { this.superviseAccountNo = superviseAccountNo; }
    public String getSuperviseAccountName() { return superviseAccountName; }
    public void setSuperviseAccountName(String superviseAccountName) { this.superviseAccountName = superviseAccountName; }
    public String getPayeeAccountNo() { return payeeAccountNo; }
    public void setPayeeAccountNo(String payeeAccountNo) { this.payeeAccountNo = payeeAccountNo; }
    public String getPayeeAccountName() { return payeeAccountName; }
    public void setPayeeAccountName(String payeeAccountName) { this.payeeAccountName = payeeAccountName; }
    public Integer getCrossBank() { return crossBank; }
    public void setCrossBank(Integer crossBank) { this.crossBank = crossBank; }
    public String getPayeeBankNo() { return payeeBankNo; }
    public void setPayeeBankNo(String payeeBankNo) { this.payeeBankNo = payeeBankNo; }
    public String getUnifiedCreditCode() { return unifiedCreditCode; }
    public void setUnifiedCreditCode(String unifiedCreditCode) { this.unifiedCreditCode = unifiedCreditCode; }
}
