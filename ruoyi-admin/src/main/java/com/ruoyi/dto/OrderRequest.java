package com.ruoyi.dto;

import java.util.List;

/**
 * 订单提交请求对象
 *
 * @author system
 * @date 2025-12-09
 */
public class OrderRequest
{
    /** 老人ID */
    private Long elderId;

    /** 机构ID */
    private Long institutionId;

    /** 房间类型（single/double/triple/vip） */
    private String roomType;

    /** 护理等级（自理/半自理/不能自理/特护） */
    private String careLevel;

    /** 餐费类型（普通餐/定制餐） */
    private String mealType;

    /** 缴纳月数 */
    private Integer months;

    /** 选择的套餐列表 */
    private List<Long> selectedPackages;

    /** 联系电话 */
    private String contactPhone;

    /** 备注信息 */
    private String remark;

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(String careLevel) {
        this.careLevel = careLevel;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public List<Long> getSelectedPackages() {
        return selectedPackages;
    }

    public void setSelectedPackages(List<Long> selectedPackages) {
        this.selectedPackages = selectedPackages;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}