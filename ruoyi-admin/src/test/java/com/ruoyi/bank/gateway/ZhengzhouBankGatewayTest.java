package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sun.net.httpserver.HttpServer;

class ZhengzhouBankGatewayTest
{
    @Test
    @EnabledIfEnvironmentVariable(named = "ZZBANK_LIVE_TEST", matches = "true")
    void shouldReachLiveSandboxWithJavaGateway()
    {
        ZhengzhouBankGateway gateway = new ZhengzhouBankGateway();
        ReflectionTestUtils.setField(gateway, "gatewayUrl", System.getenv("ZZBANK_GATEWAY_URL"));
        ReflectionTestUtils.setField(gateway, "appId", System.getenv("ZZBANK_APP_ID"));
        ReflectionTestUtils.setField(gateway, "appSecret", System.getenv("ZZBANK_APP_SECRET"));
        ReflectionTestUtils.setField(gateway, "clientPrivateKeyPath",
                System.getenv("ZZBANK_CLIENT_PRIVATE_KEY_PATH"));
        ReflectionTestUtils.setField(gateway, "bankPublicKey", System.getenv("ZZBANK_BANK_PUBLIC_KEY"));
        ReflectionTestUtils.setField(gateway, "connectTimeoutMs", 8000);
        ReflectionTestUtils.setField(gateway, "readTimeoutMs", 15000);

        BankQueryRequest request = new BankQueryRequest();
        request.setMerId("8202106040000001");
        request.setOriginalRequestNo("BP" + java.util.UUID.randomUUID().toString()
                .replace("-", "").substring(0, 30).toUpperCase());
        request.setOriginalRequestTime(new Date());

        BankResult result = gateway.queryPayment(request);

        assertEquals("FAILED", result.getStatus());
        assertEquals("1025", result.getResponseCode());

        BankResult verification = gateway.verifyMerchant("8202106040000001", "TEST_ACCOUNT");
        assertEquals("SUCCESS", verification.getStatus());
    }

    @Test
    void shouldBuildTrustedAlipayMiniProgramPayload()
    {
        ZhengzhouBankGateway gateway = new ZhengzhouBankGateway();
        ReflectionTestUtils.setField(gateway, "appId", "APP001");
        BankPaymentRequest request = new BankPaymentRequest();
        request.setRequestNo("BP123456789012345678901234567890");
        request.setMerId("8202106040000001");
        request.setAmount(new BigDecimal("0.01"));
        request.setChannelType("支付宝");
        request.setSubject("测试订单");
        request.setRequestTime(new Date());

        BankResult result = gateway.createPayment(request);

        String encoded = result.getPayUrl().substring("zzbank-alipay://".length());
        JSONObject launch = JSON.parseObject(new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8));
        String query = "&" + launch.getString("query") + "&";
        assertEquals("PENDING", result.getStatus());
        assertEquals("2021002182634333", launch.getString("appId"));
        assertTrue(query.contains("&merId=8202106040000001&"));
        assertTrue(query.contains("&txnAmt=1&"));
        assertTrue(query.contains("&obkAppId=APP001&"));
    }

    @Test
    void shouldPreferConfiguredObkAppIdOverEnvelopeAppId()
    {
        ZhengzhouBankGateway gateway = new ZhengzhouBankGateway();
        ReflectionTestUtils.setField(gateway, "appId", "APP001");
        ReflectionTestUtils.setField(gateway, "obkAppId", "obk1298393");
        BankPaymentRequest request = new BankPaymentRequest();
        request.setRequestNo("BP123456789012345678901234567890");
        request.setMerId("8202106040000001");
        request.setAmount(new BigDecimal("0.01"));
        request.setChannelType("支付宝");
        request.setSubject("测试订单");
        request.setRequestTime(new Date());

        BankResult result = gateway.createPayment(request);

        String encoded = result.getPayUrl().substring("zzbank-alipay://".length());
        JSONObject launch = JSON.parseObject(new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8));
        String query = "&" + launch.getString("query") + "&";
        assertTrue(query.contains("&obkAppId=obk1298393&"));
    }

    @Test
    void shouldSignEncryptAndVerifyBankQuery() throws Exception
    {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair clientKeys = generator.generateKeyPair();
        KeyPair bankKeys = generator.generateKeyPair();
        Path privateKeyFile = Files.createTempFile("zzbank-client-", ".key");
        Files.write(privateKeyFile, Base64.getEncoder().encode(clientKeys.getPrivate().getEncoded()));

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/openapi/zfbz/v1", exchange -> {
            try
            {
                JSONObject request = JSON.parseObject(new String(read(exchange.getRequestBody()), StandardCharsets.UTF_8));
                String plain = decrypt(request.getString("bizContent"), bankKeys.getPrivate());
                assertTrue(verify(plain, request.getString("sign"), clientKeys.getPublic()));
                assertEquals("uTxnQuery", request.getString("tranCode"));
                JSONObject query = JSON.parseObject(plain);
                boolean verification = query.getString("origTxnOrderId").startsWith("BV");

                JSONObject responseBody = new JSONObject();
                responseBody.put("respCode", "0000");
                responseBody.put("origRespCode", verification ? "1025" : "0000");
                responseBody.put("origRespMsg", verification ? "原交易不存在" : "交易成功");
                responseBody.put("origRespTxnSsn", "BANK001");
                if (!verification)
                {
                    responseBody.put("origTxnAmt", "1");
                }
                String responsePlain = JSON.toJSONString(responseBody);
                JSONObject response = new JSONObject();
                response.put("ErrorCode", "AAAAAAAAAA");
                response.put("ErrorMsg", "成功");
                response.put("sign", sign(responsePlain, bankKeys.getPrivate()));
                response.put("bizContent", encrypt(responsePlain, clientKeys.getPublic()));
                byte[] bytes = JSON.toJSONString(response).getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
            catch (Exception e)
            {
                byte[] bytes = String.valueOf(e.getMessage()).getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
            finally
            {
                exchange.close();
            }
        });
        server.start();
        try
        {
            ZhengzhouBankGateway gateway = new ZhengzhouBankGateway();
            ReflectionTestUtils.setField(gateway, "gatewayUrl", "http://127.0.0.1:"
                    + server.getAddress().getPort() + "/openapi/zfbz/v1");
            ReflectionTestUtils.setField(gateway, "appId", "APP001");
            ReflectionTestUtils.setField(gateway, "appSecret", "SECRET001");
            ReflectionTestUtils.setField(gateway, "clientPrivateKeyPath", privateKeyFile.toString());
            ReflectionTestUtils.setField(gateway, "bankPublicKey",
                    Base64.getEncoder().encodeToString(bankKeys.getPublic().getEncoded()));
            ReflectionTestUtils.setField(gateway, "connectTimeoutMs", 2000);
            ReflectionTestUtils.setField(gateway, "readTimeoutMs", 2000);

            BankQueryRequest request = new BankQueryRequest();
            request.setMerId("8202106040000001");
            request.setOriginalRequestNo("BP123456789012345678901234567890");
            request.setOriginalRequestTime(new Date());
            BankResult result = gateway.queryPayment(request);

            assertEquals("SUCCESS", result.getStatus());
            assertEquals("BANK001", result.getBankSerialNo());
            assertEquals(new BigDecimal("0.01"), result.getPaidAmount());

            BankResult verification = gateway.verifyMerchant("8202106040000001", "TEST_ACCOUNT");
            assertEquals("SUCCESS", verification.getStatus());
        }
        finally
        {
            server.stop(0);
            Files.deleteIfExists(privateKeyFile);
        }
    }

    private static String encrypt(String text, PublicKey key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
    }

    private static String decrypt(String encrypted, PrivateKey key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8);
    }

    private static String sign(String text, PrivateKey key) throws Exception
    {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(key);
        signature.update(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private static boolean verify(String text, String signed, PublicKey key) throws Exception
    {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(key);
        signature.update(text.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signed));
    }

    private static byte[] read(java.io.InputStream input) throws Exception
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int count;
        while ((count = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, count);
        }
        return output.toByteArray();
    }
}
