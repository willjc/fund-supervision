package com.ruoyi.web.controller.h5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.service.pension.IDepositApplyService;

/**
 * H5待办事项Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/todo")
public class H5TodoController extends BaseController
{
    @Autowired
    private IDepositApplyService depositApplyService;

    @Autowired
    private IElderFamilyService elderFamilyService;

    @Autowired
    private IElderInfoService elderInfoService;

    /**
     * 获取待办事项列表
     * 当前仅包含：待家属审批的押金申请
     */
    @GetMapping("/list")
    public AjaxResult getTodoList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize)
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 1. 查询当前用户关联的所有老人ID
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            if (familyList == null || familyList.isEmpty()) {
                // 用户没有关联的老人，返回空列表
                Map<String, Object> result = new HashMap<>();
                result.put("rows", new ArrayList<>());
                result.put("total", 0);
                result.put("pageNum", pageNum);
                result.put("pageSize", pageSize);
                return success(result);
            }

            // 2. 查询待审批的押金申请（状态为pending_family）
            List<Map<String, Object>> todoList = new ArrayList<>();

            for (ElderFamily family : familyList) {
                Long elderId = family.getElderId();

                // 查询该老人的押金申请
                List<DepositApply> applies = depositApplyService.selectDepositApplyByElderId(elderId);
                if (applies != null && !applies.isEmpty()) {
                    for (DepositApply apply : applies) {
                        // 只显示待家属审批的申请
                        if ("pending_family".equals(apply.getApplyStatus())) {
                            Map<String, Object> todo = new HashMap<>();
                            todo.put("type", "deposit_approve"); // 待办类型
                            todo.put("typeText", "押金审批");
                            todo.put("id", apply.getApplyId());
                            todo.put("title", "押金使用申请审批");
                            todo.put("description", apply.getPurpose() != null ? apply.getPurpose() : apply.getApplyReason());
                            todo.put("amount", apply.getApplyAmount());
                            todo.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, apply.getCreateTime()));
                            todo.put("urgencyLevel", apply.getUrgencyLevel());
                            todo.put("status", "pending");

                            // 获取老人信息
                            if (apply.getElderId() != null) {
                                ElderInfo elderInfo = elderInfoService.selectElderInfoByElderId(apply.getElderId());
                                if (elderInfo != null) {
                                    todo.put("elderName", elderInfo.getElderName());
                                }
                            }

                            // 跳转路径
                            todo.put("path", "/deposit/apply-detail/" + apply.getApplyId());

                            todoList.add(todo);
                        }
                    }
                }
            }

            // 3. 按创建时间倒序排序
            todoList.sort((t1, t2) -> {
                String time1 = (String) t1.get("createTime");
                String time2 = (String) t2.get("createTime");
                if (time2 == null || time1 == null) {
                    return 0;
                }
                return time2.compareTo(time1);
            });

            // 4. 分页处理
            int total = todoList.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);

            List<Map<String, Object>> paginatedList = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                paginatedList.add(todoList.get(i));
            }

            // 5. 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("rows", paginatedList);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            return success(result);
        } catch (Exception e) {
            logger.error("获取待办事项列表失败", e);
            return error("获取待办事项列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取待办事项统计
     */
    @GetMapping("/count")
    public AjaxResult getTodoCount()
    {
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return error("用户未登录或身份验证失败");
            }

            // 1. 查询当前用户关联的所有老人ID
            ElderFamily familyQuery = new ElderFamily();
            familyQuery.setUserId(currentUserId);
            List<ElderFamily> familyList = elderFamilyService.selectElderFamilyList(familyQuery);

            int depositApproveCount = 0;

            if (familyList != null && !familyList.isEmpty()) {
                // 2. 统计待审批的押金申请数量
                for (ElderFamily family : familyList) {
                    Long elderId = family.getElderId();
                    List<DepositApply> applies = depositApplyService.selectDepositApplyByElderId(elderId);
                    if (applies != null && !applies.isEmpty()) {
                        for (DepositApply apply : applies) {
                            if ("pending_family".equals(apply.getApplyStatus())) {
                                depositApproveCount++;
                            }
                        }
                    }
                }
            }

            // 3. 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("depositApproveCount", depositApproveCount); // 押金审批待办数量
            data.put("totalCount", depositApproveCount); // 总待办数量（目前只有押金审批）

            return success(data);
        } catch (Exception e) {
            logger.error("获取待办事项统计失败", e);
            return error("获取待办事项统计失败：" + e.getMessage());
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
