package com.ruoyi.domain.bank;

import java.util.Date;

public class BankReconRun
{
    private Long runId;
    private String merId;
    private Date clearingDate;
    private String status;
    private String fileName;
    private String billStat;
    private Integer totalRows;
    private Integer matchedRows;
    private Integer diffRows;
    private String respCode;
    private String respMessage;
    private String downloadMd5;
    private String createBy;
    private Date createTime;
    private Date updateTime;

    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public String getMerId() { return merId; }
    public void setMerId(String merId) { this.merId = merId; }
    public Date getClearingDate() { return clearingDate; }
    public void setClearingDate(Date clearingDate) { this.clearingDate = clearingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getBillStat() { return billStat; }
    public void setBillStat(String billStat) { this.billStat = billStat; }
    public Integer getTotalRows() { return totalRows; }
    public void setTotalRows(Integer totalRows) { this.totalRows = totalRows; }
    public Integer getMatchedRows() { return matchedRows; }
    public void setMatchedRows(Integer matchedRows) { this.matchedRows = matchedRows; }
    public Integer getDiffRows() { return diffRows; }
    public void setDiffRows(Integer diffRows) { this.diffRows = diffRows; }
    public String getRespCode() { return respCode; }
    public void setRespCode(String respCode) { this.respCode = respCode; }
    public String getRespMessage() { return respMessage; }
    public void setRespMessage(String respMessage) { this.respMessage = respMessage; }
    public String getDownloadMd5() { return downloadMd5; }
    public void setDownloadMd5(String downloadMd5) { this.downloadMd5 = downloadMd5; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
