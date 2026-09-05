package com.ruoyi.web.controller.bank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 已预留公网通知路由。银行通知信封、验签原文和应答格式确认前硬性关闭，
 * 不读取或记录原始报文、不调用入账服务，也不返回银行成功 ACK。
 */
@RestController
@RequestMapping("/bank/zzbank/notify")
public class BankNotificationController
{
    @PostMapping({"/payment", "/returned"})
    public ResponseEntity<Void> protocolNotVerified()
    {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
