package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资金划拨申请对象 fund_transfer_apply
 *
 * @author ruoyi
 * @date 2026-01-28
 */
public class FundTransferApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    private Long applyId;

    /** 申请单号 */
    @Excel(name = "申请单号")
    private String applyNo;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 申请总金额 */
    @Excel(name = "申请总金额")
    private BigDecimal applyAmount;

    /** 申请原因 */
    @Excel(name = "申请原因")
    private String applyReason;

    /** 紧急程度 */
    @Excel(name = "紧急程度")
    private String urgencyLevel;

    /** 期望使用日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "期望使用日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedUseDate;

    /** 附件材料 */
    private String attachments;

    /** 申请状态 draft-草稿 pending_family-待家属确认 pending_supervision-待监管审核 approved-已批准 rejected-已拒绝 completed-已完成 */
    @Excel(name = "申请状态", readConverterExp = "draft=草稿,pending_family=待家属确认,pending_supervision=待监管审核,approved=已批准,rejected=已拒绝,completed=已完成")
    private String applyStatus;

    /** 家属确认人 */
    @Excel(name = "家属确认人")
    private String familyConfirmName;

    /** 家属关系 */
    @Excel(name = "家属关系")
    private String familyRelation;

    /** 家属电话 */
    @Excel(name = "家属电话")
    private String familyPhone;

    /** 家属审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date familyApproveTime;

    /** 家属审核意见 */
    private String familyApproveOpinion;

    /** 监管审核人 */
    @Excel(name = "监管审核人")
    private String approver;

    /** 监管审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "监管审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 监管审核意见 */
    private String approveRemark;

    /** 实际批准金额 */
    @Excel(name = "实际批准金额")
    private BigDecimal actualAmount;

    /** 实际划拨时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    // 关联字段（用于展示）
    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 老人身份证 */
    private String elderIdCard;

    /** 床位信息 */
    private String bedInfo;

    /** 关联划拨单号列表（逗号分隔） */
    private String transferNos;

    /** 数据权限过滤：当前用户ID（不对应数据库字段） */
    private transient Long currentUserId;

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyNo(String applyNo)
    {
        this.applyNo = applyNo;
    }

    public String getApplyNo()
    {
        return applyNo;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }

    public void setApplyAmount(BigDecimal applyAmount)
    {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getApplyAmount()
    {
        return applyAmount;
    }

    public void setApplyReason(String applyReason)
    {
        this.applyReason = applyReason;
    }

    public String getApplyReason()
    {
        return applyReason;
    }

    public void setUrgencyLevel(String urgencyLevel)
    {
        this.urgencyLevel = urgencyLevel;
    }

    public String getUrgencyLevel()
    {
        return urgencyLevel;
    }

    public void setExpectedUseDate(Date expectedUseDate)
    {
        this.expectedUseDate = expectedUseDate;
    }

    public Date getExpectedUseDate()
    {
        return expectedUseDate;
    }

    public void setAttachments(String attachments)
    {
        this.attachments = attachments;
    }

    public String getAttachments()
    {
        return attachments;
    }

    public void setApplyStatus(String applyStatus)
    {
        this.applyStatus = applyStatus;
    }

    public String getApplyStatus()
    {
        return applyStatus;
    }

    public void setFamilyConfirmName(String familyConfirmName)
    {
        this.familyConfirmName = familyConfirmName;
    }

    public String getFamilyConfirmName()
    {
        return familyConfirmName;
    }

    public void setFamilyRelation(String familyRelation)
    {
        this.familyRelation = familyRelation;
    }

    public String getFamilyRelation()
    {
        return familyRelation;
    }

    public void setFamilyPhone(String familyPhone)
    {
        this.familyPhone = familyPhone;
    }

    public String getFamilyPhone()
    {
        return familyPhone;
    }

    public void setFamilyApproveTime(Date familyApproveTime)
    {
        this.familyApproveTime = familyApproveTime;
    }

    public Date getFamilyApproveTime()
    {
        return familyApproveTime;
    }

    public void setFamilyApproveOpinion(String familyApproveOpinion)
    {
        this.familyApproveOpinion = familyApproveOpinion;
    }

    public String getFamilyApproveOpinion()
    {
        return familyApproveOpinion;
    }

    public void setApprover(String approver)
    {
        this.approver = approver;
    }

    public String getApprover()
    {
        return approver;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setApproveRemark(String approveRemark)
    {
        this.approveRemark = approveRemark;
    }

    public String getApproveRemark()
    {
        return approveRemark;
    }

    public void setActualAmount(BigDecimal actualAmount)
    {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getActualAmount()
    {
        return actualAmount;
    }

    public void setUseTime(Date useTime)
    {
        this.useTime = useTime;
    }

    public Date getUseTime()
    {
        return useTime;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getElderIdCard() {
        return elderIdCard;
    }

    public void setElderIdCard(String elderIdCard) {
        this.elderIdCard = elderIdCard;
    }

    public String getBedInfo() {
        return bedInfo;
    }

    public void setBedInfo(String bedInfo) {
        this.bedInfo = bedInfo;
    }

    public String getTransferNos() {
        return transferNos;
    }

    public void setTransferNos(String transferNos) {
        this.transferNos = transferNos;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }
}
