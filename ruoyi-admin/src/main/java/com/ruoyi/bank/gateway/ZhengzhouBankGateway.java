package com.ruoyi.bank.gateway;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;

@Component
@ConditionalOnProperty(prefix = "bank.integration", name = "mode", havingValue = "zzbank")
public class ZhengzhouBankGateway implements BankGateway
{
    private static final String ALIPAY_MINI_APP_ID = "2021002182634333";

    @Value("${bank.integration.gateway-url:https://obk.lovingfox.cn:7000/openapi/zfbz/v1}")
    private String gatewayUrl;

    @Value("${bank.integration.app-id:}")
    private String appId;

    // 开放银行 appId：银行小程序拉起参数使用 obk 形态标识（见郑银支付小程序说明），
    // 与 openapi 报文信封的数字 appId 可能不同，未配置时回退 appId。
    @Value("${bank.integration.obk-app-id:}")
    private String obkAppId;

    @Value("${bank.integration.app-secret:}")
    private String appSecret;

    @Value("${bank.integration.client-private-key-path:}")
    private String clientPrivateKeyPath;

    @Value("${bank.integration.bank-public-key:}")
    private String bankPublicKey;

    @Value("${bank.integration.callback-url:}")
    private String callbackUrl;

    @Value("${bank.integration.mini-program-md5-key:}")
    private String miniProgramMd5Key;

    @Value("${bank.integration.connect-timeout-ms:8000}")
    private int connectTimeoutMs;

    @Value("${bank.integration.read-timeout-ms:15000}")
    private int readTimeoutMs;

    @Value("${bank.integration.allow-private-gateway:false}")
    private boolean allowPrivateGateway;

    @Override
    public BankResult createPayment(BankPaymentRequest request)
    {
        if (!"支付宝".equals(request.getChannelType()))
        {
            throw new ServiceException("微信小程序支付的 fivem 规则尚未明确，当前测试仅支持支付宝");
        }
        require(appId, "郑州银行 appId 未配置");
        if (request.getRequestTime() == null)
        {
            throw new ServiceException("银行支付请求时间为空");
        }

        JSONObject query = new JSONObject();
        query.put("txnType", "1007");
        query.put("txnSubType", "100704");
        query.put("aesWay", "01");
        query.put("merId", request.getMerId());
        query.put("txnOrderId", request.getRequestNo());
        query.put("txnOrderTime", format(request.getRequestTime(), "yyyyMMddHHmmss"));
        query.put("txnAmt", request.getAmount().movePointRight(2)
                .setScale(0, RoundingMode.UNNECESSARY).toPlainString());
        query.put("subAppId", ALIPAY_MINI_APP_ID);
        query.put("txnOrderBody", truncate(request.getSubject(), 64));
        query.put("txnOrderDetail", truncate(request.getSubject(), 128));
        query.put("txnCcyType", "156");
        query.put("payChl", "OBK");
        query.put("obkAppId", isBlank(obkAppId) ? appId : obkAppId);
        query.put("istest", "1");
        query.put("dev", "uatb");
        if (!isBlank(callbackUrl))
        {
            query.put("backEndUrl", callbackUrl);
        }
        require(miniProgramMd5Key, "银行小程序 MD5 密钥未配置");
        JSONObject reqData = new JSONObject();
        reqData.put("fivem", signMiniProgram(query, miniProgramMd5Key));
        query.put("reqData", Base64.getEncoder().encodeToString(
                JSON.toJSONString(reqData).getBytes(StandardCharsets.UTF_8)));

        JSONObject launch = new JSONObject();
        launch.put("appId", ALIPAY_MINI_APP_ID);
        launch.put("page", "pages/index/index");
        launch.put("query", query.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&")));
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(JSON.toJSONString(launch).getBytes(StandardCharsets.UTF_8));
        return BankResult.pending(null, "zzbank-alipay://" + encoded);
    }

    // 银行补充确认：排除空值、reqData、istest、dev；原值排序后追加完整商户密钥。
    // V0.6 示例使用小写摘要，空白键和值不参与签名。
    static String signMiniProgram(JSONObject query, String key)
    {
        String plain = query.entrySet().stream()
                .filter(entry -> !"reqData".equalsIgnoreCase(entry.getKey())
                        && !"istest".equalsIgnoreCase(entry.getKey()) && !"dev".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> org.apache.commons.lang3.StringUtils.isNotBlank(entry.getKey())
                        && entry.getValue() != null
                        && org.apache.commons.lang3.StringUtils.isNotBlank(entry.getValue().toString()))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .sorted()
                .collect(Collectors.joining("&"));
        return org.springframework.util.DigestUtils.md5DigestAsHex((plain + key).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public BankResult queryPayment(BankQueryRequest request)
    {
        requireProtocolConfig();
        if (request.getOriginalRequestTime() == null)
        {
            throw new ServiceException("原支付请求时间为空，无法向银行查单");
        }

        Date now = new Date();
        JSONObject bizContent = new JSONObject();
        bizContent.put("merId", request.getMerId());
        bizContent.put("txnOrderId", "BQ" + IdUtils.fastSimpleUUID().substring(0, 30).toUpperCase());
        bizContent.put("txnOrderTime", format(now, "yyyyMMddHHmmss"));
        bizContent.put("origTxnOrderId", request.getOriginalRequestNo());
        bizContent.put("origTxnOrderTime", format(request.getOriginalRequestTime(), "yyyyMMddHHmmss"));
        if (!isBlank(request.getBankSerialNo()))
        {
            bizContent.put("origRespTxnSsn", request.getBankSerialNo());
        }
        bizContent.put("aesWay", "01");

        JSONObject response = post("uTxnQuery", bizContent, now);
        String queryCode = normalizeCode(response.getString("respCode"));
        if (!"0000".equals(queryCode))
        {
            // 查询请求失败不等于原支付失败，不能据此释放资金或重新支付。
            return BankResult.unknown(queryCode, response.getString("respMsg"));
        }
        String originalCode = normalizeCode(response.getString("origRespCode"));
        if ("1025".equals(originalCode))
        {
            return BankResult.unknown(originalCode, "银行暂未查到原交易，请稍后查询或人工核查");
        }
        boolean success = "0000".equals(originalCode);
        String originalOrder = response.getString("origTxnOrderId");
        if ((success && isBlank(originalOrder))
                || (!isBlank(originalOrder) && !originalOrder.equals(request.getOriginalRequestNo())))
        {
            return BankResult.unknown("ORDER_MISMATCH", "银行查单原交易订单号缺失或不匹配");
        }
        String originalTime = response.getString("origTxnOrderTime");
        if ((success && isBlank(originalTime)) || (!isBlank(originalTime)
                && !format(request.getOriginalRequestTime(), "yyyyMMddHHmmss").equals(originalTime)))
        {
            return BankResult.unknown("ORDER_TIME_MISMATCH", "银行查单原交易订单时间缺失或不匹配");
        }
        String bankSerialNo = response.getString("origRespTxnSsn");
        if (!isBlank(request.getBankSerialNo()) && (success || !isBlank(bankSerialNo))
                && !request.getBankSerialNo().equals(bankSerialNo))
        {
            return BankResult.unknown("SERIAL_MISMATCH", "银行查单原交易流水与已保存流水不匹配");
        }
        return result(originalCode, response.getString("origRespMsg"), response);
    }

    @Override
    public BankResult submitPayout(BankPayoutRequest request)
    {
        payoutBody(request);
        // 业务字段已确认，但监管网关、信封和终态语义未确认。不能复用收单 zfbz 网关猜测发送。
        return BankGateway.super.submitPayout(request);
    }

    @Override
    public BankResult queryPayout(BankPayoutRequest request)
    {
        request.validateQuery();
        return BankGateway.super.queryPayout(request);
    }

    /** v1.3 的 ylzjhb 业务字段；不代表监管报文信封已经获得银行确认。 */
    JSONObject payoutBody(BankPayoutRequest request)
    {
        request.validate();
        JSONObject body = new JSONObject();
        body.put("trandt", format(request.getRequestTime(), "yyyyMMdd"));
        body.put("trantm", format(request.getRequestTime(), "HHmmss"));
        body.put("paylno", request.getRequestNo());
        body.put("jgacct", request.getPayerAccountNo());
        body.put("jgacna", request.getPayerAccountName());
        body.put("pyeeac", request.getPayeeAccountNo());
        body.put("pyeena", request.getPayeeAccountName());
        if (!isBlank(request.getPayeeBankNo()))
        {
            body.put("pyeebk", request.getPayeeBankNo());
        }
        body.put("crcycd", "156");
        body.put("tranam", request.getAmount().setScale(2, RoundingMode.UNNECESSARY));
        if (!isBlank(request.getRemark()))
        {
            body.put("remark", request.getRemark());
        }
        return body;
    }

    @Override
    public BankResult refundPayment(BankRefundRequest request)
    {
        requireProtocolConfig();
        requirePublicGatewayUrl();
        request.validate();

        JSONObject bizContent = new JSONObject();
        bizContent.put("merId", request.getMerId());
        if (!isBlank(request.getMerName()))
        {
            bizContent.put("merName", request.getMerName());
        }
        bizContent.put("txnOrderId", request.getRequestNo());
        bizContent.put("txnOrderTime", format(request.getRequestTime(), "yyyyMMddHHmmss"));
        bizContent.put("origRespTxnSsn", request.getOriginalBankSerialNo());
        bizContent.put("origRespTxnTime", request.getOriginalBankTime());
        bizContent.put("txnAmt", request.getAmount().movePointRight(2)
                .setScale(0, RoundingMode.UNNECESSARY).toPlainString());
        bizContent.put("aesWay", "01");

        // 受理成功不代表退款完成；终态一律通过 uTxnQuery 查询退款请求确认。
        JSONObject response = post("uTxnRefund", bizContent, request.getRequestTime());
        String code = normalizeCode(response.getString("respCode"));
        if ("0000".equals(code))
        {
            BankResult result = BankResult.pending(response.getString("respTxnSsn"), null);
            result.setResponseCode(code);
            result.setResponseMessage("退款已受理，等待查询确认");
            return result;
        }
        if (Arrays.asList("0002", "0003", "0004", "0005", "0007", "0008", "0009",
                "0010", "0011", "0012", "0013", "0014").contains(code))
        {
            BankResult result = BankResult.pending(null, null);
            result.setResponseCode(code);
            result.setResponseMessage(response.getString("respMsg"));
            return result;
        }
        return BankResult.failed(code, response.getString("respMsg"));
    }

    /** 出网请求前校验网关地址：仅 http/https，拒绝环回、私有和保留地址（测试可用开关放行）。 */
    private void requirePublicGatewayUrl()
    {
        if (allowPrivateGateway)
        {
            return;
        }
        URL url;
        try
        {
            url = new URL(gatewayUrl);
        }
        catch (java.net.MalformedURLException e)
        {
            throw new ServiceException("银行网关地址无效：" + gatewayUrl);
        }
        String protocol = url.getProtocol();
        if (!"http".equals(protocol) && !"https".equals(protocol))
        {
            throw new ServiceException("银行网关地址仅支持 http/https");
        }
        java.net.InetAddress address;
        try
        {
            address = java.net.InetAddress.getByName(url.getHost());
        }
        catch (java.net.UnknownHostException e)
        {
            throw new ServiceException("银行网关域名无法解析，拒绝发送请求");
        }
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isSiteLocalAddress()
                || address.isLinkLocalAddress() || address.isMulticastAddress())
        {
            throw new ServiceException("银行网关地址不允许指向本机、私有或保留网络");
        }
    }

    @Override
    public BankResult verifyMerchant(String merId, String settlementAccountNo)
    {
        require(merId, "银行商户号不能为空");
        require(settlementAccountNo, "监管账户不能为空");
        BankQueryRequest request = new BankQueryRequest();
        request.setMerId(merId);
        request.setOriginalRequestNo("BV" + IdUtils.fastSimpleUUID().substring(0, 30).toUpperCase());
        request.setOriginalRequestTime(new Date());
        BankResult result = queryPayment(request);
        if ("1025".equals(result.getResponseCode()))
        {
            BankResult verified = BankResult.success(null);
            verified.setResponseMessage("测试网关已接受该商户号；监管账户绑定以银行配置为准");
            return verified;
        }
        return BankResult.failed(result.getResponseCode(), "测试网关未确认该商户号："
                + result.getResponseMessage());
    }

    JSONObject post(String tranCode, JSONObject body, Date now)
    {
        try
        {
            PrivateKey privateKey = loadPrivateKey();
            PublicKey publicKey = loadBankPublicKey();

            byte[] payload = JSON.toJSONString(buildEnvelope(tranCode, body, now)).getBytes(StandardCharsets.UTF_8);
            HttpURLConnection connection = (HttpURLConnection) new URL(gatewayUrl).openConnection();
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true);
            try (OutputStream output = connection.getOutputStream())
            {
                output.write(payload);
            }

            int status = connection.getResponseCode();
            InputStream input = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String responseText = input == null ? "" : read(input);
            if (status != HttpURLConnection.HTTP_OK)
            {
                throw new ServiceException("郑州银行网关 HTTP 状态异常：" + status);
            }

            JSONObject envelope = JSON.parseObject(responseText);
            if (!"AAAAAAAAAA".equals(envelope.getString("ErrorCode")))
            {
                throw new ServiceException("郑州银行网关拒绝请求：" + envelope.getString("ErrorCode")
                        + " " + envelope.getString("ErrorMsg"));
            }
            String responsePlainText = decrypt(envelope.getString("bizContent"), privateKey);
            if (!verify(responsePlainText, envelope.getString("sign"), publicKey))
            {
                throw new ServiceException("郑州银行响应验签失败");
            }
            return JSON.parseObject(responsePlainText);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("郑州银行查单失败：" + e.getMessage());
        }
    }

    /** 开放银行信封：仅 bizContent 参与签名与加密；tranSeq 优先取业务订单号，对账类无单号报文按规则生成。 */
    JSONObject buildEnvelope(String tranCode, JSONObject body, Date now) throws Exception
    {
        String plainText = JSON.toJSONString(body);
        PrivateKey privateKey = loadPrivateKey();
        PublicKey publicKey = loadBankPublicKey();

        JSONObject request = new JSONObject();
        request.put("tranDate", format(now, "yyyyMMdd"));
        request.put("tranTime", format(now, "HHmmss"));
        String tranSeq = body.getString("txnOrderId");
        request.put("tranSeq", isBlank(tranSeq)
                ? "BC" + IdUtils.fastSimpleUUID().substring(0, 30).toUpperCase() : tranSeq);
        request.put("tranCode", tranCode);
        request.put("sign", sign(plainText, privateKey));
        request.put("securityType", "rsa");
        request.put("appId", appId);
        request.put("appSecret", appSecret);
        request.put("bizContent", encrypt(plainText, publicKey));
        return request;
    }

    /** uMtBillApply：申请生成指定清算日期的对账单。respCode 0000 成功；1073 无交易；1081 跑批未完成；1082 未结算。 */
    public JSONObject applyBill(String merId, String clearingDate)
    {
        requireProtocolConfig();
        requirePublicGatewayUrl();
        requireClearingDate(clearingDate);
        JSONObject body = new JSONObject();
        body.put("merId", merId);
        body.put("dateStlm", clearingDate);
        body.put("payChl", "OBK");
        body.put("obkAppId", isBlank(obkAppId) ? appId : obkAppId);
        return post("uMtBillApply", body, new Date());
    }

    /** uMtBillQuery：查询对账单生成标志（billStat 00 未生成 / 01 已生成）。 */
    public JSONObject queryBillStatus(String merId, String clearingDate)
    {
        requireProtocolConfig();
        requirePublicGatewayUrl();
        requireClearingDate(clearingDate);
        JSONObject body = new JSONObject();
        body.put("merId", merId);
        body.put("dateStlm", clearingDate);
        body.put("payChl", "OBK");
        body.put("obkAppId", isBlank(obkAppId) ? appId : obkAppId);
        return post("uMtBillQuery", body, new Date());
    }

    /** sddzfiledown（新版 newsddzfiledown/v1）：按文件名下载对账文件，响应体为文件、响应头 sign 为文件 MD5。 */
    public byte[] downloadBillFile(String fileName)
    {
        requireProtocolConfig();
        requirePublicGatewayUrl();
        if (isBlank(fileName))
        {
            throw new ServiceException("对账文件名为空");
        }
        String downloadUrl = gatewayUrl.replace("zfbz/v1", "newsddzfiledown/v1");
        JSONObject body = new JSONObject();
        body.put("fileName", fileName);
        try
        {
            PrivateKey privateKey = loadPrivateKey();
            byte[] payload = JSON.toJSONString(buildEnvelope("sddzfiledown", body, new Date()))
                    .getBytes(StandardCharsets.UTF_8);
            HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs * 4);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true);
            try (OutputStream output = connection.getOutputStream())
            {
                output.write(payload);
            }
            int status = connection.getResponseCode();
            InputStream input = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            byte[] content = input == null ? new byte[0] : readBytes(input);
            if (status != HttpURLConnection.HTTP_OK)
            {
                throw new ServiceException("对账文件下载 HTTP 状态异常：" + status);
            }
            String errorCode = connection.getHeaderField("ErrorCode");
            String errorMsg = connection.getHeaderField("ErrorMsg");
            String sign = connection.getHeaderField("sign");
            if (!"AAAAAAAAAA".equals(errorCode))
            {
                throw new ServiceException("对账文件下载被网关拒绝：" + errorCode + " " + errorMsg);
            }
            String actualMd5 = org.springframework.util.DigestUtils.md5DigestAsHex(content);
            if (isBlank(sign) || !actualMd5.equalsIgnoreCase(sign.trim()))
            {
                throw new ServiceException("对账文件 MD5 校验失败，文件可能不完整");
            }
            return content;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("对账文件下载失败：" + e.getMessage());
        }
    }

    private void requireClearingDate(String clearingDate)
    {
        if (isBlank(clearingDate) || !clearingDate.matches("[0-9]{8}"))
        {
            throw new ServiceException("清算日期格式必须为 yyyyMMdd");
        }
    }

    private BankResult result(String code, String message, JSONObject response)
    {
        String bankSerialNo = response.getString("origRespTxnSsn");
        if ("EMPTY_CODE".equals(code) || !code.matches("[0-9]{4}"))
        {
            return BankResult.unknown(code, "银行未返回可识别的原交易状态");
        }
        if ("0000".equals(code))
        {
            String amount = response.getString("origTxnAmt");
            if (isBlank(amount) || !amount.matches("[0-9]{1,12}") || new BigDecimal(amount).signum() <= 0)
            {
                return BankResult.unknown("AMOUNT_INVALID", "银行成功响应未返回有效的原交易金额（整数分）");
            }
            if (isBlank(bankSerialNo))
            {
                return BankResult.unknown("SERIAL_MISSING", "银行成功响应未返回原交易流水");
            }
            BankResult result = BankResult.success(bankSerialNo);
            result.setResponseMessage(message);
            result.setPaidAmount(new BigDecimal(amount).movePointLeft(2));
            result.setBankTransactionTime(response.getString("origRespTxnTime"));
            return result;
        }
        if (Arrays.asList("0002", "0003", "0004", "0005", "0007", "0008", "0009",
                "0010", "0011", "0012", "0013", "0014").contains(code))
        {
            BankResult result = BankResult.pending(bankSerialNo, null);
            result.setResponseCode(code);
            result.setResponseMessage(message);
            return result;
        }
        return BankResult.failed(code, message);
    }

    private String encrypt(String plainText, PublicKey key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(chunk(cipher, plainText.getBytes(StandardCharsets.UTF_8),
                (((RSAKey) key).getModulus().bitLength() + 7) / 8 - 11));
    }

    private String decrypt(String encrypted, PrivateKey key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(chunk(cipher, Base64.getDecoder().decode(encrypted),
                (((RSAKey) key).getModulus().bitLength() + 7) / 8), StandardCharsets.UTF_8);
    }

    private byte[] chunk(Cipher cipher, byte[] input, int chunkSize) throws Exception
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (int offset = 0; offset < input.length; offset += chunkSize)
        {
            output.write(cipher.doFinal(input, offset, Math.min(chunkSize, input.length - offset)));
        }
        return output.toByteArray();
    }

    private String sign(String text, PrivateKey key) throws Exception
    {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(key);
        signature.update(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private boolean verify(String text, String signed, PublicKey key) throws Exception
    {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(key);
        signature.update(text.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signed));
    }

    private PrivateKey loadPrivateKey() throws Exception
    {
        String text = new String(Files.readAllBytes(Paths.get(clientPrivateKeyPath)), StandardCharsets.UTF_8);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decodeKey(text)));
    }

    private PublicKey loadBankPublicKey() throws Exception
    {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodeKey(bankPublicKey)));
    }

    private byte[] decodeKey(String value)
    {
        return Base64.getDecoder().decode(value.replaceAll("-----[^-]+-----", "").replaceAll("\\s", ""));
    }

    private void requireProtocolConfig()
    {
        require(gatewayUrl, "郑州银行测试网关未配置");
        require(appId, "郑州银行 appId 未配置");
        require(appSecret, "郑州银行 appSecret 未配置");
        require(clientPrivateKeyPath, "应用私钥文件路径未配置");
        require(bankPublicKey, "银行平台公钥未配置");
    }

    private void require(String value, String message)
    {
        if (isBlank(value))
        {
            throw new ServiceException(message);
        }
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeCode(String code)
    {
        if (isBlank(code))
        {
            return "EMPTY_CODE";
        }
        return code.length() == 7 ? code.substring(3) : code;
    }

    private String format(Date date, String pattern)
    {
        return new SimpleDateFormat(pattern).format(date);
    }

    private String truncate(String value, int maxLength)
    {
        if (value == null || value.length() <= maxLength)
        {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private byte[] readBytes(InputStream input) throws Exception
    {
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream())
        {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = stream.read(buffer)) != -1)
            {
                output.write(buffer, 0, count);
            }
            return output.toByteArray();
        }
    }

    private String read(InputStream input) throws Exception
    {
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream())
        {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = stream.read(buffer)) != -1)
            {
                output.write(buffer, 0, count);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
