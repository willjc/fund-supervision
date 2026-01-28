package com.ruoyi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digital.szzz.gateway.sdk.api.GatewaySender;
import com.digital.szzz.gateway.sdk.bean.GatewayResponse;
import com.digital.szzz.gateway.sdk.util.AesUtil;
import com.digital.szzz.gateway.sdk.util.SignUtil;
import com.ruoyi.service.IZhengHaoBanService;
import com.ruoyi.web.core.domain.ZhbUserInfo;
import com.ruoyi.web.core.config.ZhengHaoBanConfig;

import java.nio.charset.StandardCharsets;

/**
 * 郑好办服务实现
 *
 * @author ruoyi
 */
@Service
public class ZhengHaoBanServiceImpl implements IZhengHaoBanService
{
    private static final Logger log = LoggerFactory.getLogger(ZhengHaoBanServiceImpl.class);

    private static final String METHOD_OAUTH_TOKEN = "szzz.uc.api.oauth.token";
    private static final String METHOD_USER_DETAIL = "szzz.uc.api.userdetail.get";

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    @Autowired
    private ZhengHaoBanConfig zhbConfig;

    @Override
    public String getOAuthToken(String authCode)
    {
        log.info("getOAuthToken called with authCode: {}", authCode);

        try
        {
            ZhengHaoBanConfig.GatewayConfig gateway = zhbConfig.getGateway();
            String moduleId = zhbConfig.getApp().getModuleId();

            // 构建请求参数
            JSONObject json = new JSONObject();
            json.put("moduleId", moduleId);
            json.put("grantType", "authorization_code");
            json.put("authCode", authCode);
            String body = json.toJSONString();

            // 调用网关
            GatewayResponse response = GatewaySender.send(
                    gateway.getAppId(),
                    METHOD_OAUTH_TOKEN,
                    gateway.getIvStr(),
                    gateway.getKeyStr(),
                    gateway.getSecretKey(),
                    body,
                    CONNECT_TIMEOUT,
                    READ_TIMEOUT
            );

            if (response.getCode() != 200)
            {
                log.error("Gateway request failed, code: {}, message: {}", response.getCode(), response.getMessage());
                return null;
            }

            // 验证签名
            String expectSign = response.getSign();
            String factSign = SignUtil.signResponse(response, gateway.getSecretKey());
            if (!expectSign.equals(factSign))
            {
                log.error("Signature verification failed, expect: {}, fact: {}", expectSign, factSign);
                return null;
            }

            // 解密响应数据
            byte[] data = AesUtil.base64ToByte(response.getData());
            byte[] plainData = AesUtil.decrypt(
                    AesUtil.base64ToByte(gateway.getIvStr()),
                    AesUtil.base64ToByte(gateway.getKeyStr()),
                    data
            );
            String plainText = new String(plainData, StandardCharsets.UTF_8);
            log.info("OAuth token response: {}", plainText);

            // 解析JSON获取oauthToken
            JSONObject result = JSON.parseObject(plainText);
            return result.getString("oauthToken");

        }
        catch (Exception e)
        {
            log.error("Failed to get OAuth token", e);
            return null;
        }
    }

    @Override
    public ZhbUserInfo getUserDetail(String oauthToken)
    {
        log.info("getUserDetail called with oauthToken: {}", oauthToken);

        try
        {
            ZhengHaoBanConfig.GatewayConfig gateway = zhbConfig.getGateway();
            String moduleId = zhbConfig.getApp().getModuleId();

            // 构建请求参数
            JSONObject json = new JSONObject();
            json.put("moduleId", moduleId);
            json.put("oauthToken", oauthToken);
            String body = json.toJSONString();

            // 调用网关
            GatewayResponse response = GatewaySender.send(
                    gateway.getAppId(),
                    METHOD_USER_DETAIL,
                    gateway.getIvStr(),
                    gateway.getKeyStr(),
                    gateway.getSecretKey(),
                    body,
                    CONNECT_TIMEOUT,
                    READ_TIMEOUT
            );

            if (response.getCode() != 200)
            {
                log.error("Gateway request failed, code: {}, message: {}", response.getCode(), response.getMessage());
                return null;
            }

            // 验证签名
            String expectSign = response.getSign();
            String factSign = SignUtil.signResponse(response, gateway.getSecretKey());
            if (!expectSign.equals(factSign))
            {
                log.error("Signature verification failed, expect: {}, fact: {}", expectSign, factSign);
                return null;
            }

            // 解密响应数据
            byte[] data = AesUtil.base64ToByte(response.getData());
            byte[] plainData = AesUtil.decrypt(
                    AesUtil.base64ToByte(gateway.getIvStr()),
                    AesUtil.base64ToByte(gateway.getKeyStr()),
                    data
            );
            String plainText = new String(plainData, StandardCharsets.UTF_8);
            log.info("User detail response: {}", plainText);

            // 解析JSON获取用户信息
            return parseUserInfo(plainText);

        }
        catch (Exception e)
        {
            log.error("Failed to get user detail", e);
            return null;
        }
    }

    @Override
    public ZhbUserInfo loginByAuthCode(String authCode)
    {
        log.info("loginByAuthCode called with authCode: {}", authCode);

        // 先获取oauthToken
        String oauthToken = getOAuthToken(authCode);
        if (oauthToken == null)
        {
            log.error("Failed to get oauthToken");
            return null;
        }

        // 再获取用户信息
        return getUserDetail(oauthToken);
    }

    /**
     * 解析用户信息JSON
     */
    private ZhbUserInfo parseUserInfo(String jsonStr)
    {
        try
        {
            JSONObject json = JSON.parseObject(jsonStr);
            ZhbUserInfo userInfo = new ZhbUserInfo();

            // 基础信息
            userInfo.setZid(json.getString("zid"));
            userInfo.setUid(json.getString("uid"));
            userInfo.setPhone(json.getString("phone"));

            // 名称信息
            userInfo.setDisplayName(json.getString("displayName"));
            userInfo.setRealName(json.getString("realName"));

            // 身份信息
            userInfo.setIdCode(json.getString("idCode"));

            // 其他信息
            userInfo.setAvatarUrl(json.getString("avatarUrl"));

            // 性别 (0:男 1:女)
            String gender = json.getString("gender");
            if (gender != null)
            {
                userInfo.setGender(Integer.parseInt(gender));
            }

            log.info("Parsed user info: zid={}, phone={}, displayName={}",
                    userInfo.getZid(), userInfo.getPhone(), userInfo.getDisplayName());

            return userInfo;
        }
        catch (Exception e)
        {
            log.error("Failed to parse user info JSON", e);
            return null;
        }
    }
}
