package com.ruoyi.web.controller.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.bank.BankPaymentCompletionResult;
import com.ruoyi.service.bank.IMockBankPaymentService;

/**
 * 测试环境模拟支付入口。非 mock 模式下该控制器不会注册。
 */
@RestController
@RequestMapping("/h5/payment/mock")
@ConditionalOnProperty(prefix = "bank.integration", name = "mode", havingValue = "mock")
public class H5MockBankPaymentController extends BaseController
{
    @Autowired
    private IMockBankPaymentService mockBankPaymentService;

    @PostMapping("/complete/{requestNo}")
    public AjaxResult complete(@PathVariable String requestNo)
    {
        try
        {
            Long userId = SecurityUtils.getUserId();
            BankPaymentCompletionResult result = mockBankPaymentService.completeMockPayment(requestNo, userId);
            return success(result);
        }
        catch (ServiceException e)
        {
            return error(e.getMessage());
        }
        catch (Exception e)
        {
            return error("用户未登录或模拟支付完成失败");
        }
    }
}
