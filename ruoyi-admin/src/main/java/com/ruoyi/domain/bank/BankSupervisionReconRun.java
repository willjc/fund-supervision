package com.ruoyi.domain.bank;

import java.util.Date;

public class BankSupervisionReconRun
{
    private Long runId;
    private Long institutionId;
    private String accountNo;
    private String accountName;
    private Date trandt;
    private String status;
    private String fileName;
    private Long fileSize;
    private String fileMd5;
    private String savedPath;
    private String respCode;
    private String respMsg;
    private Integer totalRows;
    private Integer matchedRows;
    private Integer diffRows;
    private Integer retryCount;
    private String createBy;
    private Date createTime;
    private Date updateTime;

    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public Long getInstitutionId() { return institutionId; }
    public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public Date getTrandt() { return trandt; }
    public void setTrandt(Date trandt) { this.trandt = trandt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getFileMd5() { return fileMd5; }
    public void setFileMd5(String fileMd5) { this.fileMd5 = fileMd5; }
    public String getSavedPath() { return savedPath; }
    public void setSavedPath(String savedPath) { this.savedPath = savedPath; }
    public String getRespCode() { return respCode; }
    public void setRespCode(String respCode) { this.respCode = respCode; }
    public String getRespMsg() { return respMsg; }
    public void setRespMsg(String respMsg) { this.respMsg = respMsg; }
    public Integer getTotalRows() { return totalRows; }
    public void setTotalRows(Integer totalRows) { this.totalRows = totalRows; }
    public Integer getMatchedRows() { return matchedRows; }
    public void setMatchedRows(Integer matchedRows) { this.matchedRows = matchedRows; }
    public Integer getDiffRows() { return diffRows; }
    public void setDiffRows(Integer diffRows) { this.diffRows = diffRows; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
