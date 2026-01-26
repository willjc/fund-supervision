package com.ruoyi.web.controller.h5;

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
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.pension.PensionComplaint;
import com.ruoyi.service.IPensionComplaintService;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.framework.config.ServerConfig;

/**
 * H5投诉建议Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/complaint")
public class H5ComplaintController extends BaseController
{
    @Autowired
    private IPensionComplaintService complaintService;

    @Autowired
    private IPensionInstitutionService institutionService;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 提交投诉
     *
     * @param params 投诉信息
     * @return 结果
     */
    @PostMapping("/submit")
    @Log(title = "H5提交投诉", businessType = BusinessType.INSERT)
    public AjaxResult submitComplaint(@RequestBody Map<String, Object> params)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return AjaxResult.error("用户未登录");
        }

        SysUser user = loginUser.getUser();
        Long userId = user.getUserId();

        // 获取参数
        Long institutionId = params.get("institutionId") != null ?
                Long.parseLong(params.get("institutionId").toString()) : null;
        String complaintType = (String) params.get("complaintType");
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String images = (String) params.get("images");
        String contactName = (String) params.get("contactName");
        String contactPhone = (String) params.get("contactPhone");

        // 参数校验
        if (institutionId == null)
        {
            return AjaxResult.error("请选择投诉机构");
        }
        if (title == null || title.trim().isEmpty())
        {
            return AjaxResult.error("请输入投诉标题");
        }
        if (content == null || content.trim().isEmpty())
        {
            return AjaxResult.error("请输入投诉内容");
        }
        if (contactName == null || contactName.trim().isEmpty())
        {
            return AjaxResult.error("请输入联系人");
        }
        if (contactPhone == null || contactPhone.trim().isEmpty())
        {
            return AjaxResult.error("请输入联系电话");
        }

        // 获取机构信息
        PensionInstitution institution = institutionService.selectPensionInstitutionByInstitutionId(institutionId);
        if (institution == null)
        {
            return AjaxResult.error("机构不存在");
        }

        // 创建投诉
        PensionComplaint complaint = new PensionComplaint();
        complaint.setInstitutionId(institutionId);
        complaint.setInstitutionName(institution.getInstitutionName());
        complaint.setUserId(userId);
        complaint.setComplaintType(complaintType);
        complaint.setTitle(title);
        complaint.setContent(content);
        if (StringUtils.isNotEmpty(images))
        {
            complaint.setImages(images);
        }
        complaint.setContactName(contactName);
        complaint.setContactPhone(contactPhone);
        complaint.setStatus(PensionComplaint.STATUS_PROCESSING);

        int result = complaintService.insertPensionComplaint(complaint);
        if (result > 0)
        {
            AjaxResult ajax = AjaxResult.success("投诉提交成功");
            ajax.put("complaintNo", complaint.getComplaintNo());
            ajax.put("complaintId", complaint.getComplaintId());
            return ajax;
        }
        return AjaxResult.error("投诉提交失败");
    }

    /**
     * 查询我的投诉列表
     *
     * @return 投诉列表
     */
    @GetMapping("/list")
    public AjaxResult getMyComplaintList()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return AjaxResult.error("用户未登录");
        }

        SysUser user = loginUser.getUser();
        Long userId = user.getUserId();

        PensionComplaint query = new PensionComplaint();
        query.setUserId(userId);

        List<PensionComplaint> list = complaintService.selectPensionComplaintList(query);

        // 转换为前端需要的格式
        List<Map<String, Object>> resultList = list.stream()
                .map(this::convertToH5Format)
                .collect(Collectors.toList());

        return AjaxResult.success(resultList);
    }

    /**
     * 查询投诉详情
     *
     * @param complaintId 投诉ID
     * @return 投诉详情
     */
    @GetMapping("/detail")
    public AjaxResult getComplaintDetail(@RequestParam Long complaintId)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return AjaxResult.error("用户未登录");
        }

        SysUser user = loginUser.getUser();
        Long userId = user.getUserId();

        PensionComplaint complaint = complaintService.selectPensionComplaintById(complaintId);
        if (complaint == null)
        {
            return AjaxResult.error("投诉不存在");
        }

        // 验证权限
        if (!complaint.getUserId().equals(userId))
        {
            return AjaxResult.error("无权查看该投诉");
        }

        return AjaxResult.success(convertToH5Format(complaint));
    }

    /**
     * 上传投诉图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload")
    @Log(title = "H5投诉图片上传", businessType = BusinessType.OTHER)
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

        // 返回完整的图片URL（包含服务器地址），避免前端图片请求携带过大的Cookie
        String fullImageUrl = serverConfig.getUrl() + imageUrl;

        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", fullImageUrl);
        return ajax;
    }

    /**
     * 转换为H5前端需要的格式
     */
    private Map<String, Object> convertToH5Format(PensionComplaint complaint)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("id", complaint.getComplaintId());
        result.put("complaintNo", complaint.getComplaintNo());
        result.put("institutionId", complaint.getInstitutionId());
        result.put("institutionName", complaint.getInstitutionName());
        result.put("complaintType", complaint.getComplaintType());
        result.put("title", complaint.getTitle());
        result.put("content", complaint.getContent());

        // 处理图片数组，转换为完整的URL
        String[] imageArray = complaint.getImageArray();
        String[] fullImageUrls = new String[imageArray.length];
        String serverUrl = serverConfig.getUrl();
        for (int i = 0; i < imageArray.length; i++) {
            String img = imageArray[i];
            // 如果是相对路径（以/profile开头），添加服务器地址
            if (img != null && !img.isEmpty() && !img.startsWith("http")) {
                fullImageUrls[i] = serverUrl + img;
            } else {
                fullImageUrls[i] = img;
            }
        }
        result.put("images", fullImageUrls);

        result.put("contactName", complaint.getContactName());
        result.put("contactPhone", complaint.getContactPhone());

        // 状态转换（前端需要的状态值）
        String status = complaint.getStatus();
        String frontendStatus = "pending"; // 默认待处理
        if (PensionComplaint.STATUS_PROCESSING.equals(status))
        {
            frontendStatus = "processing";
        }
        else if (PensionComplaint.STATUS_PROCESSED.equals(status))
        {
            frontendStatus = "completed";
        }
        else if (PensionComplaint.STATUS_REJECTED.equals(status))
        {
            frontendStatus = "rejected";
        }
        result.put("status", frontendStatus);
        result.put("statusText", complaint.getStatusText());

        // 回复内容
        result.put("replyContent", complaint.getReplyContent());

        // 时间格式化
        if (complaint.getCreateTime() != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result.put("createTime", sdf.format(complaint.getCreateTime()));
        }
        if (complaint.getHandleTime() != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result.put("handleTime", sdf.format(complaint.getHandleTime()));
        }

        return result;
    }
}
