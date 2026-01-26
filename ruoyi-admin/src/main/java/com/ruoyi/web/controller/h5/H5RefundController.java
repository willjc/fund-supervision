package com.ruoyi.web.controller.h5;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.domain.pension.RefundRecord;
import com.ruoyi.service.pension.IRefundRecordService;
import com.ruoyi.framework.config.ServerConfig;

/**
 * H5退款申请Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/refund")
public class H5RefundController extends BaseController
{
    @Autowired
    private IRefundRecordService refundRecordService;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 提交退款申请
     *
     * @param params 退款信息
     * @return 结果
     */
    @PostMapping("/apply")
    @Log(title = "H5提交退款申请", businessType = BusinessType.INSERT)
    public AjaxResult applyRefund(@RequestBody Map<String, Object> params)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return AjaxResult.error("用户未登录");
        }

        SysUser user = loginUser.getUser();
        Long userId = user.getUserId();

        // 获取参数
        Long elderId = params.get("elderId") != null ?
                Long.parseLong(params.get("elderId").toString()) : null;
        Long institutionId = params.get("institutionId") != null ?
                Long.parseLong(params.get("institutionId").toString()) : null;

        Object serviceAmountObj = params.get("serviceRefundAmount");
        Object depositAmountObj = params.get("depositRefundAmount");
        Object memberAmountObj = params.get("memberRefundAmount");

        BigDecimal serviceRefundAmount = serviceAmountObj != null ?
                new BigDecimal(serviceAmountObj.toString()) : BigDecimal.ZERO;
        BigDecimal depositRefundAmount = depositAmountObj != null ?
                new BigDecimal(depositAmountObj.toString()) : BigDecimal.ZERO;
        BigDecimal memberRefundAmount = memberAmountObj != null ?
                new BigDecimal(memberAmountObj.toString()) : BigDecimal.ZERO;

        String refundReason = (String) params.get("refundReason");
        String refundDesc = (String) params.get("refundDesc");
        String evidenceImages = (String) params.get("evidenceImages");

        // 参数校验
        if (elderId == null)
        {
            return AjaxResult.error("请选择老人");
        }
        if (institutionId == null)
        {
            return AjaxResult.error("请选择养老机构");
        }

        // 计算总退款金额
        BigDecimal totalRefundAmount = serviceRefundAmount.add(depositRefundAmount).add(memberRefundAmount);
        if (totalRefundAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return AjaxResult.error("退款金额必须大于0");
        }

        // 检查退款金额是否为负数
        if (serviceRefundAmount.compareTo(BigDecimal.ZERO) < 0 ||
            depositRefundAmount.compareTo(BigDecimal.ZERO) < 0 ||
            memberRefundAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            return AjaxResult.error("退款金额不能为负数");
        }

        if (refundReason == null || refundReason.trim().isEmpty())
        {
            return AjaxResult.error("请选择退款原因");
        }

        // 创建退款申请
        RefundRecord refundRecord = new RefundRecord();
        refundRecord.setElderId(elderId);
        refundRecord.setInstitutionId(institutionId);
        refundRecord.setOrderId(0L); // 针对老人账户的退款，order_id设为0
        refundRecord.setPaymentId(0L);
        refundRecord.setRefundAmount(totalRefundAmount);
        refundRecord.setServiceRefundAmount(serviceRefundAmount);
        refundRecord.setDepositRefundAmount(depositRefundAmount);
        refundRecord.setMemberRefundAmount(memberRefundAmount);
        refundRecord.setRefundReason(refundReason);
        refundRecord.setRefundDesc(refundDesc);
        refundRecord.setRefundStatus("0"); // 待处理
        refundRecord.setRefundMethod("账户退款");
        refundRecord.setCreateBy(String.valueOf(userId));
        refundRecord.setCreateTime(new Date());

        if (StringUtils.isNotEmpty(evidenceImages))
        {
            refundRecord.setEvidenceImages(evidenceImages);
        }

        // 生成退款单号
        String refundNo = "REF" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + userId;
        refundRecord.setRefundNo(refundNo);

        int result = refundRecordService.insertRefundRecord(refundRecord);
        if (result > 0)
        {
            AjaxResult ajax = AjaxResult.success("退款申请提交成功");
            ajax.put("refundNo", refundRecord.getRefundNo());
            ajax.put("refundId", refundRecord.getRefundId());
            return ajax;
        }
        return AjaxResult.error("退款申请提交失败");
    }

    /**
     * 查询我的退款列表
     *
     * @return 退款列表
     */
    @GetMapping("/list")
    public AjaxResult getMyRefundList()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return AjaxResult.error("用户未登录");
        }

        SysUser user = loginUser.getUser();
        Long userId = user.getUserId();

        // 查询当前用户的退款申请（通过创建者筛选）
        RefundRecord query = new RefundRecord();
        query.setCreateBy(String.valueOf(userId));

        List<RefundRecord> list = refundRecordService.selectRefundRecordList(query);

        // 转换为前端需要的格式
        List<Map<String, Object>> resultList = list.stream()
                .map(this::convertToH5Format)
                .collect(Collectors.toList());

        return AjaxResult.success(resultList);
    }

    /**
     * 查询退款详情
     *
     * @param refundId 退款ID
     * @return 退款详情
     */
    @GetMapping("/detail")
    public AjaxResult getRefundDetail(@RequestParam Long refundId)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return AjaxResult.error("用户未登录");
        }

        SysUser user = loginUser.getUser();
        Long userId = user.getUserId();

        RefundRecord refundRecord = refundRecordService.selectRefundRecordByRefundId(refundId);
        if (refundRecord == null)
        {
            return AjaxResult.error("退款记录不存在");
        }

        // 验证权限（只能查看自己创建的退款申请）
        if (!String.valueOf(userId).equals(refundRecord.getCreateBy()))
        {
            return AjaxResult.error("无权查看该退款记录");
        }

        return AjaxResult.success(convertToH5Format(refundRecord));
    }

    /**
     * 上传退款凭证图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload")
    @Log(title = "H5退款凭证上传", businessType = BusinessType.OTHER)
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file) throws Exception
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return AjaxResult.error("用户未登录");
        }

        if (file.isEmpty())
        {
            return AjaxResult.error("请选择要上传的文件");
        }

        // 上传图片文件
        String imageUrl = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);

        // 返回完整的图片URL
        String fullImageUrl = serverConfig.getUrl() + imageUrl;

        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", fullImageUrl);
        return ajax;
    }

    /**
     * 转换为H5前端需要的格式
     */
    private Map<String, Object> convertToH5Format(RefundRecord refundRecord)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("id", refundRecord.getRefundId());
        result.put("refundNo", refundRecord.getRefundNo());
        result.put("elderId", refundRecord.getElderId());
        result.put("elderName", refundRecord.getElderName());
        result.put("institutionId", refundRecord.getInstitutionId());
        result.put("institutionName", refundRecord.getInstitutionName());
        result.put("refundAmount", refundRecord.getRefundAmount());
        result.put("serviceRefundAmount", refundRecord.getServiceRefundAmount() != null ?
                refundRecord.getServiceRefundAmount() : BigDecimal.ZERO);
        result.put("depositRefundAmount", refundRecord.getDepositRefundAmount() != null ?
                refundRecord.getDepositRefundAmount() : BigDecimal.ZERO);
        result.put("memberRefundAmount", refundRecord.getMemberRefundAmount() != null ?
                refundRecord.getMemberRefundAmount() : BigDecimal.ZERO);
        result.put("refundReason", refundRecord.getRefundReason());
        result.put("refundDesc", refundRecord.getRefundDesc());

        // 处理凭证图片数组
        String evidenceImages = refundRecord.getEvidenceImages();
        if (StringUtils.isNotEmpty(evidenceImages))
        {
            try {
                String[] imageArray = evidenceImages.split(",");
                String[] fullImageUrls = new String[imageArray.length];
                String serverUrl = serverConfig.getUrl();
                for (int i = 0; i < imageArray.length; i++) {
                    String img = imageArray[i].trim();
                    // 如果是相对路径，添加服务器地址
                    if (img.startsWith("/profile") || img.startsWith("profile")) {
                        fullImageUrls[i] = serverUrl + (img.startsWith("/") ? "" : "/") + img;
                    } else if (!img.startsWith("http")) {
                        fullImageUrls[i] = serverUrl + "/" + img;
                    } else {
                        fullImageUrls[i] = img;
                    }
                }
                result.put("evidenceImages", fullImageUrls);
            } catch (Exception e) {
                result.put("evidenceImages", new String[0]);
            }
        } else {
            result.put("evidenceImages", new String[0]);
        }

        // 状态转换
        String status = refundRecord.getRefundStatus();
        String statusText = "待处理";
        if ("0".equals(status))
        {
            statusText = "待审核";
        }
        else if ("1".equals(status))
        {
            statusText = "已通过";
        }
        else if ("2".equals(status))
        {
            statusText = "已拒绝";
        }
        result.put("refundStatus", status);
        result.put("statusText", statusText);

        // 审批信息
        result.put("approver", refundRecord.getApprover());
        result.put("approveTime", refundRecord.getApproveTime());
        result.put("approveRemark", refundRecord.getApproveRemark());

        // 时间格式化
        if (refundRecord.getCreateTime() != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result.put("createTime", sdf.format(refundRecord.getCreateTime()));
        }

        return result;
    }
}
