package com.ruoyi.domain.bank;

import java.math.BigDecimal;
import java.util.Date;

public class BankReconDiff
{
    private Long diffId;
    private Long runId;
    private String diffType;
    private String requestNo;
    private String bankSerialNo;
    private BigDecimal bankAmount;
    private String bankDetail;
    private BigDecimal localAmount;
    private String localSerialNo;
    private String localStatus;
    private Integer handled;
    private String handleRemark;
    private Date createTime;

    public Long getDiffId() { return diffId; }
    public void setDiffId(Long diffId) { this.diffId = diffId; }
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public String getDiffType() { return diffType; }
    public void setDiffType(String diffType) { this.diffType = diffType; }
    public String getRequestNo() { return requestNo; }
    public void setRequestNo(String requestNo) { this.requestNo = requestNo; }
    public String getBankSerialNo() { return bankSerialNo; }
    public void setBankSerialNo(String bankSerialNo) { this.bankSerialNo = bankSerialNo; }
    public BigDecimal getBankAmount() { return bankAmount; }
    public void setBankAmount(BigDecimal bankAmount) { this.bankAmount = bankAmount; }
    public String getBankDetail() { return bankDetail; }
    public void setBankDetail(String bankDetail) { this.bankDetail = bankDetail; }
    public BigDecimal getLocalAmount() { return localAmount; }
    public void setLocalAmount(BigDecimal localAmount) { this.localAmount = localAmount; }
    public String getLocalSerialNo() { return localSerialNo; }
    public void setLocalSerialNo(String localSerialNo) { this.localSerialNo = localSerialNo; }
    public String getLocalStatus() { return localStatus; }
    public void setLocalStatus(String localStatus) { this.localStatus = localStatus; }
    public Integer getHandled() { return handled; }
    public void setHandled(Integer handled) { this.handled = handled; }
    public String getHandleRemark() { return handleRemark; }
    public void setHandleRemark(String handleRemark) { this.handleRemark = handleRemark; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
