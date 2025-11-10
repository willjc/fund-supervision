package com.ruoyi.web.controller.pension;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 老人账户信息Controller
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/pension/account")
public class AccountInfoController extends BaseController
{
    @Autowired
    private IAccountInfoService accountInfoService;

    /**
     * 查询老人账户信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(AccountInfo accountInfo)
    {
        startPage();
        List<AccountInfo> list = accountInfoService.selectAccountInfoList(accountInfo);
        return getDataTable(list);
    }

    /**
     * 导出老人账户信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:account:export')")
    @Log(title = "老人账户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AccountInfo accountInfo)
    {
        List<AccountInfo> list = accountInfoService.selectAccountInfoList(accountInfo);
        ExcelUtil<AccountInfo> util = new ExcelUtil<AccountInfo>(AccountInfo.class);
        util.exportExcel(response, list, "老人账户信息数��");
    }

    /**
     * 获取老人账户信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:account:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") Long accountId)
    {
        return success(accountInfoService.selectAccountInfoByAccountId(accountId));
    }

    /**
     * 根据老人ID查询账户信息
     */
    @PreAuthorize("@ss.hasPermi('pension:account:query')")
    @GetMapping(value = "/elder/{elderId}")
    public AjaxResult getAccountByElderId(@PathVariable("elderId") Long elderId)
    {
        return success(accountInfoService.selectAccountInfoByElderId(elderId));
    }

    /**
     * 根据机构ID查询账户列表
     */
    @PreAuthorize("@ss.hasPermi('pension:account:list')")
    @GetMapping(value = "/institution/{institutionId}")
    public AjaxResult getAccountByInstitutionId(@PathVariable("institutionId") Long institutionId)
    {
        List<AccountInfo> list = accountInfoService.selectAccountInfoByInstitutionId(institutionId);
        return success(list);
    }

    /**
     * 新增老人账户信息
     */
    @PreAuthorize("@ss.hasPermi('pension:account:add')")
    @Log(title = "老人账户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AccountInfo accountInfo)
    {
        return toAjax(accountInfoService.insertAccountInfo(accountInfo));
    }

    /**
     * 修改老人账户信息
     */
    @PreAuthorize("@ss.hasPermi('pension:account:edit')")
    @Log(title = "老人账户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AccountInfo accountInfo)
    {
        return toAjax(accountInfoService.updateAccountInfo(accountInfo));
    }

    /**
     * 删除老人账户信息
     */
    @PreAuthorize("@ss.hasPermi('pension:account:remove')")
    @Log(title = "老人账户信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds)
    {
        return toAjax(accountInfoService.deleteAccountInfoByAccountIds(accountIds));
    }

    /**
     * 统计机构账户余额
     */
    @PreAuthorize("@ss.hasPermi('pension:account:query')")
    @GetMapping("/balance/summary")
    public AjaxResult getBalanceSummary(@RequestParam(required = false) Long institutionId)
    {
        Map<String, Object> summary = accountInfoService.selectAccountBalanceSummary(institutionId);
        return success(summary);
    }

    /**
     * 查询余额不足的老人账户
     */
    @PreAuthorize("@ss.hasPermi('pension:account:list')")
    @GetMapping("/balance/low/{months}")
    public AjaxResult getLowBalanceAccounts(@PathVariable("months") Integer months)
    {
        List<AccountInfo> list = accountInfoService.selectLowBalanceAccounts(months);
        return success(list);
    }

    /**
     * 冻结/解冻账户
     */
    @PreAuthorize("@ss.hasPermi('pension:account:edit')")
    @Log(title = "账户状态变更", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{accountId}/{accountStatus}")
    public AjaxResult changeAccountStatus(@PathVariable("accountId") Long accountId,
                                       @PathVariable("accountStatus") String accountStatus)
    {
        return toAjax(accountInfoService.changeAccountStatus(accountId, accountStatus));
    }

    /**
     * 销户
     */
    @PreAuthorize("@ss.hasPermi('pension:account:remove')")
    @Log(title = "账户销户", businessType = BusinessType.UPDATE)
    @PutMapping("/close/{accountId}")
    public AjaxResult closeAccount(@PathVariable("accountId") Long accountId)
    {
        return toAjax(accountInfoService.closeAccount(accountId));
    }
}