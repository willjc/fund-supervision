package com.ruoyi.web.controller.h5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.domain.pension.FundTransferApply;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.service.pension.IDepositApplyService;
import com.ruoyi.service.pension.IFundTransferApplyService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.mapper.pension.FundTransferApplyDetailMapper;
import com.ruoyi.domain.pension.FundTransferApplyDetail;

/**
 * H5押金管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/deposit")
public class H5DepositController extends BaseController
{
    @Autowired
    private IDepositApplyService depositApplyService;

    @Autowired
    private IElderFamilyService elderFamilyService;

    @Autowired
    private IElderInfoService elderInfoService;

    @Autowired
    private IPensionInstitutionService institutionService;

    @Autowired
    private IAccountInfoService accountInfoService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IFundTransferApplyService fundTransferApplyService;

    @Autowired
    private FundTransferApplyDetailMapper fundTransferApplyDetailMapper;

    /**
     * 获取押金申请列表（家属视角）
     * 查询当前用户关联的所有老人的押金申请，支持按老人ID筛选
     */
    @GetMapping("/apply/list")
    public AjaxResult getApplyList(@RequestParam(required = false) Long elderId,
                                   @RequestParam(required = false) String applyStatus,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 1. 查询当前用户关联的老人ID
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);

            // 如果指定了elderId，验证该老人是否属于当前用户
            if (elderId != null) {
                familyQuery.setElderId(elderId);
            }

            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                // 用户没有关联的老人，或指定的老人不属于当前用户
                Map<String, Object> result = new HashMap<>();
                result.put("rows", new ArrayList<>());
                result.put("total", 0);
                result.put("pageNum", pageNum);
                result.put("pageSize", pageSize);
                return success(result);
            }

            // 2. 查询这些老人的押金申请
            List<DepositApply> allApplies = new ArrayList<>();
            for (ElderFamily family : familyList) {
                Long queryElderId = family.getElderId();
                List<DepositApply> applies = depositApplyService.selectDepositApplyByElderId(queryElderId);
                if (applies != null && !applies.isEmpty()) {
                    allApplies.addAll(applies);
                }
            }

            // 3. 按状态筛选
            List<DepositApply> filteredList = new ArrayList<>();
            for (DepositApply apply : allApplies) {
                // 如果指定了状态，只返回该状态的申请
                if (applyStatus != null && !applyStatus.isEmpty() && !"all".equals(applyStatus)) {
                    if (applyStatus.equals(apply.getApplyStatus())) {
                        filteredList.add(apply);
                    }
                } else {
                    filteredList.add(apply);
                }
            }

            // 4. 按申请时间倒序排序
            filteredList.sort((a1, a2) -> {
                if (a2.getCreateTime() == null || a1.getCreateTime() == null) {
                    return 0;
                }
                return a2.getCreateTime().compareTo(a1.getCreateTime());
            });

            // 5. 分页处理
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, filteredList.size());
            List<DepositApply> paginatedList = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                paginatedList.add(filteredList.get(i));
            }

            // 6. 构建返回数据
            List<Map<String, Object>> rows = new ArrayList<>();
            for (DepositApply apply : paginatedList) {
                Map<String, Object> item = new HashMap<>();
                item.put("applyId", apply.getApplyId());
                item.put("applyNo", apply.getApplyNo());
                item.put("applyAmount", apply.getApplyAmount());
                item.put("applyReason", apply.getApplyReason());
                item.put("applyType", apply.getApplyType());
                item.put("applyStatus", apply.getApplyStatus());
                item.put("applyStatusText", getApplyStatusText(apply.getApplyStatus()));
                item.put("urgencyLevel", apply.getUrgencyLevel());
                item.put("purpose", apply.getPurpose());
                item.put("usagePurpose", apply.getPurpose()); // 前端期望的字段名
                item.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, apply.getCreateTime()));

                // 获取老人信息
                if (apply.getElderId() != null) {
                    ElderInfo elderInfo = elderInfoService.selectElderInfoByElderId(apply.getElderId());
                    if (elderInfo != null) {
                        item.put("elderName", elderInfo.getElderName());
                        item.put("elderIdCard", elderInfo.getIdCard());
                    }
                }

                // 获取机构信息
                if (apply.getInstitutionId() != null) {
                    PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(apply.getInstitutionId());
                    if (institution != null) {
                        item.put("institutionName", institution.getInstitutionName());
                    }
                }

                rows.add(item);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("rows", rows);
            result.put("total", filteredList.size());
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            return success(result);
        } catch (Exception e) {
            logger.error("获取押金申请列表失败", e);
            return error("获取押金申请列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取押金申请详情
     */
    @GetMapping("/apply/{applyId}")
    public AjaxResult getApplyDetail(@PathVariable("applyId") Long applyId)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 查询申请信息
            DepositApply apply = depositApplyService.selectDepositApplyByApplyId(applyId);
            if (apply == null) {
                return error("押金申请不存在");
            }

            // 验证用户是否为该老人的家属
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            familyQuery.setElderId(apply.getElderId());
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                return error("您不是该老人的家属，无权查看此申请");
            }

            // 构建返回数据（字段名与前端保持一致）
            Map<String, Object> data = new HashMap<>();
            data.put("applyId", apply.getApplyId());
            data.put("applyNo", apply.getApplyNo());
            data.put("applyAmount", apply.getApplyAmount());
            data.put("applyReason", apply.getApplyReason());
            data.put("applyType", apply.getApplyType());
            data.put("applyStatus", apply.getApplyStatus());
            data.put("applyStatusText", getApplyStatusText(apply.getApplyStatus()));
            data.put("urgencyLevel", apply.getUrgencyLevel());
            data.put("usagePurpose", apply.getPurpose()); // 前端用的是 usagePurpose
            data.put("description", apply.getDescription());
            data.put("expectedUseDate", apply.getExpectedUseDate() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, apply.getExpectedUseDate()) : "");

            // 解析附件JSON字符串
            String attachmentsStr = apply.getAttachments();
            if (attachmentsStr != null && !attachmentsStr.isEmpty()) {
                try {
                    // 如果是JSON数组字符串，尝试解析
                    data.put("attachments", com.alibaba.fastjson2.JSON.parseArray(attachmentsStr));
                } catch (Exception e) {
                    // 解析失败，返回空数组
                    data.put("attachments", new ArrayList<>());
                }
            } else {
                data.put("attachments", new ArrayList<>());
            }

            data.put("createTime", apply.getCreateTime());

            // 申请人（显示昵称而不是登录名）
            String createBy = apply.getCreateBy();
            if (createBy != null && !createBy.isEmpty()) {
                SysUser user = userService.selectUserByUserName(createBy);
                if (user != null && user.getNickName() != null && !user.getNickName().isEmpty()) {
                    data.put("createBy", user.getNickName());
                } else {
                    data.put("createBy", createBy);
                }
            } else {
                data.put("createBy", "-");
            }

            // 家属审批信息（字段名与前端保持一致）
            data.put("familyApprovalUser", apply.getFamilyConfirmName()); // 前端用的是 familyApprovalUser
            data.put("familyRelation", apply.getFamilyRelation());
            data.put("familyPhone", apply.getFamilyPhone());
            data.put("familyApprovalTime", apply.getFamilyApproveTime()); // 前端用的是 familyApprovalTime

            // 根据审批意见判断结果
            String familyOpinion = apply.getFamilyApproveOpinion();
            if (familyOpinion != null) {
                if ("approved".equals(familyOpinion) || "agree".equals(familyOpinion) || familyOpinion.contains("同意")) {
                    data.put("familyApprovalResult", "approved");
                } else if ("rejected".equals(familyOpinion) || "reject".equals(familyOpinion) || familyOpinion.contains("拒绝")) {
                    data.put("familyApprovalResult", "rejected");
                    data.put("familyRejectReason", familyOpinion);
                }
            }

            // 监管审批信息（字段名与前端保持一致）
            data.put("supervisionApprovalUser", apply.getApprover()); // 前端用的是 supervisionApprovalUser
            data.put("supervisionApprovalTime", apply.getApproveTime()); // 前端用的是 supervisionApprovalTime

            // 根据审批备注判断结果
            String approveRemark = apply.getApproveRemark();
            if (approveRemark != null) {
                if (approveRemark.contains("通过") || approveRemark.contains("同意")) {
                    data.put("supervisionApprovalResult", "approved");
                } else if (approveRemark.contains("拒绝") || approveRemark.contains("驳回")) {
                    data.put("supervisionApprovalResult", "rejected");
                }
            }

            // 使用信息
            data.put("actualAmount", apply.getActualAmount());
            data.put("useTime", apply.getUseTime());

            // 获取老人信息
            if (apply.getElderId() != null) {
                ElderInfo elderInfo = elderInfoService.selectElderInfoByElderId(apply.getElderId());
                if (elderInfo != null) {
                    data.put("elderName", elderInfo.getElderName());
                    data.put("elderIdCard", elderInfo.getIdCard());
                    data.put("elderPhone", elderInfo.getPhone());
                }
            }

            // 床位信息（Mapper已通过联表查询返回，格式：房间号-床位号）
            String bedInfo = apply.getBedInfo();
            if (bedInfo != null && !bedInfo.isEmpty()) {
                // 后台格式：101-101-2，转换为：101室101-2床
                String[] parts = bedInfo.split("-");
                if (parts.length >= 2) {
                    data.put("bedInfo", parts[0] + "室" + parts[1] + "床");
                } else {
                    data.put("bedInfo", bedInfo);
                }
            } else {
                data.put("bedInfo", "-");
            }

            // 获取机构信息
            if (apply.getInstitutionId() != null) {
                PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(apply.getInstitutionId());
                if (institution != null) {
                    data.put("institutionId", institution.getInstitutionId());
                    data.put("institutionName", institution.getInstitutionName());
                    data.put("institutionPhone", institution.getContactPhone());
                    data.put("institutionAddress", institution.getActualAddress());
                }
            }

            // 获取账户信息（押金申请显示押金余额）
            if (apply.getAccountId() != null) {
                AccountInfo accountInfo = accountInfoService.selectAccountInfoByAccountId(apply.getAccountId());
                if (accountInfo != null) {
                    // 押金申请，accountBalance 显示押金余额
                    data.put("accountBalance", accountInfo.getDepositBalance());
                    data.put("depositBalance", accountInfo.getDepositBalance());
                    data.put("prepaidBalance", accountInfo.getServiceBalance());
                }
            }

            return success(data);
        } catch (Exception e) {
            logger.error("获取押金申请详情失败", e);
            return error("获取押金申请详情失败：" + e.getMessage());
        }
    }

    /**
     * 家属审批押金申请
     */
    @PostMapping("/apply/family/approve")
    public AjaxResult familyApprove(@RequestBody Map<String, Object> params)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 获取参数
            Long applyId = Long.parseLong(params.get("applyId").toString());
            String approvalResult = params.get("approvalResult").toString();
            String rejectReason = params.get("rejectReason") != null ? params.get("rejectReason").toString() : "";

            // 查询申请信息
            DepositApply apply = depositApplyService.selectDepositApplyByApplyId(applyId);
            if (apply == null) {
                return error("押金申请不存在");
            }

            // 验证用户是否为该老人的家属
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            familyQuery.setElderId(apply.getElderId());
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                return error("您不是该老人的家属，无权审批此申请");
            }

            // 验证申请状态
            if (!"pending_family".equals(apply.getApplyStatus())) {
                return error("当前状态不允许家属审批");
            }

            // 获取家属信息（用于记录）- 优先使用真实姓名，其次昵称
            String familyName = SecurityUtils.getUsername();
            try {
                SysUser user = userService.selectUserById(currentUserId);
                if (user != null) {
                    if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
                        familyName = user.getRealName();
                    } else if (user.getNickName() != null && !user.getNickName().trim().isEmpty()) {
                        familyName = user.getNickName();
                    }
                }
            } catch (Exception e) {
                logger.warn("获取用户真实姓名失败，使用用户名: {}", familyName);
            }

            // 调用家属审批方法
            // 直接传递明确的审批标志，不使用文本内容判断
            String opinion = "approved".equals(approvalResult) ? "approved" : "rejected";
            int result = depositApplyService.familyApprove(applyId, opinion, familyName, rejectReason);

            if (result > 0) {
                return success("审批成功");
            } else {
                return error("审批失败");
            }
        } catch (Exception e) {
            logger.error("家属审批失败", e);
            return error("家属审批失败：" + e.getMessage());
        }
    }

    /**
     * 家属审批资金划拨申请
     */
    @PostMapping("/transfer/family/approve")
    public AjaxResult approveTransfer(@RequestBody Map<String, Object> params)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 获取参数
            Long applyId = Long.parseLong(params.get("applyId").toString());
            String approvalResult = params.get("approvalResult").toString();
            String rejectReason = params.get("rejectReason") != null ? params.get("rejectReason").toString() : "";

            // 查询划拨申请信息
            FundTransferApply apply = fundTransferApplyService.selectFundTransferApplyByApplyId(applyId);
            if (apply == null) {
                return error("划拨申请不存在");
            }

            // 验证用户是否为该老人的家属
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            familyQuery.setElderId(apply.getElderId());
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                return error("您不是该老人的家属，无权审批此申请");
            }

            // 获取家属信息
            SysUser user = SecurityUtils.getLoginUser().getUser();
            String approverName = user.getNickName() != null ? user.getNickName() : user.getUserName();
            String relation = familyList.get(0).getRelationName();
            String phone = familyList.get(0).getPhonenumber();

            // 调用家属审批方法
            boolean approved = "approved".equals(approvalResult);
            String opinion = approved ? "同意" : rejectReason;
            int result = fundTransferApplyService.familyApprove(applyId, approved, opinion, approverName, relation, phone);

            if (result > 0) {
                return success(approved ? "审批成功，等待监管部门审批" : "已拒绝申请");
            } else {
                return error("审批失败");
            }
        } catch (Exception e) {
            logger.error("家属审批划拨申请失败", e);
            return error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 获取资金划拨申请详情（家属端）
     */
    @GetMapping("/transfer/detail/{applyId}")
    public AjaxResult getTransferDetail(@PathVariable Long applyId)
    {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            FundTransferApply apply = fundTransferApplyService.selectFundTransferApplyByApplyId(applyId);
            if (apply == null) {
                return error("划拨申请不存在");
            }

            // 验证用户是否为该老人的家属
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            familyQuery.setElderId(apply.getElderId());
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                return error("您不是该老人的家属，无权查看此申请");
            }

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("applyId", apply.getApplyId());
            data.put("applyNo", apply.getApplyNo());
            data.put("applyAmount", apply.getApplyAmount());
            data.put("applyReason", apply.getApplyReason());
            data.put("urgencyLevel", apply.getUrgencyLevel());
            data.put("expectedUseDate", apply.getExpectedUseDate() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, apply.getExpectedUseDate()) : "");
            data.put("applyStatus", apply.getApplyStatus());
            data.put("createTime", apply.getCreateTime() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, apply.getCreateTime()) : "");
            data.put("familyApproveOpinion", apply.getFamilyApproveOpinion());
            data.put("familyApproveTime", apply.getFamilyApproveTime() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, apply.getFamilyApproveTime()) : "");
            data.put("familyConfirmName", apply.getFamilyConfirmName());
            data.put("approveRemark", apply.getApproveRemark());
            data.put("approveTime", apply.getApproveTime() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, apply.getApproveTime()) : "");
            data.put("approver", apply.getApprover());

            // 获取老人信息
            ElderInfo elderInfo = elderInfoService.selectElderInfoByElderId(apply.getElderId());
            if (elderInfo != null) {
                data.put("elderName", elderInfo.getElderName());
            }

            // 获取机构信息
            if (apply.getInstitutionId() != null) {
                PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(apply.getInstitutionId());
                if (institution != null) {
                    data.put("institutionName", institution.getInstitutionName());
                }
            }

            // 获取划拨明细
            List<FundTransferApplyDetail> transferDetails = fundTransferApplyDetailMapper.selectFundTransferApplyDetailByApplyId(applyId);
            List<Map<String, Object>> detailsList = new ArrayList<>();
            if (transferDetails != null && !transferDetails.isEmpty()) {
                for (FundTransferApplyDetail detail : transferDetails) {
                    Map<String, Object> detailItem = new HashMap<>();
                    detailItem.put("billingMonth", detail.getBillingMonth());
                    detailItem.put("transferAmount", detail.getTransferAmount());
                    detailItem.put("transferNo", detail.getTransferNo());
                    detailsList.add(detailItem);
                }
            }
            data.put("transferDetails", detailsList);

            return success(data);
        } catch (Exception e) {
            logger.error("获取划拨申请详情失败", e);
            return error("获取详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取申请状态文本
     */
    private String getApplyStatusText(String status)
    {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case "draft":
                return "草稿";
            case "pending_family":
                return "待家属审批";
            case "family_approved":
                return "家属已审批";
            case "pending_supervision":
                return "待监管审批";
            case "approved":
                return "已通过";
            case "rejected":
                return "已驳回";
            case "withdrawn":
                return "已撤回";
            default:
                return "未知";
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId()
    {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            logger.error("获取当前用户ID失败", e);
            return null;
        }
    }
}
