// payment/src/main/java/com/self/appreciation/payment/util/AlipayUtil.java
package com.self.appreciation.payment.util;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.self.appreciation.payment.config.AlipayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlipayUtil {

    private final AlipayConfig alipayConfig;

    @PostConstruct
    public void init() throws IOException {
        Config config = new Config();
        config.protocol = "https";
        // config.gatewayHost = alipayConfig.getGatewayUrl(); // 生产环境
        config.gatewayHost = "openapi-sandbox.dl.alipaydev.com"; // 沙箱环境
        config.signType = "RSA2";
        config.appId = alipayConfig.getAppId();
        config.merchantPrivateKey = Files.readString(Path.of(alipayConfig.getPrivateKey())); // 自己生成的私钥
        config.alipayPublicKey = Files.readString(Path.of(alipayConfig.getAlipayPublicKey())); // 支付宝返回的公钥
        config.notifyUrl = alipayConfig.getNotifyUrl(); // 支付结果异步通知地址
        Factory.setOptions(config);
    }

    /**
     * 生成商户订单号
     */
    public String generateOutTradeNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 调用支付宝统一下单接口
     */
    public String pagePay(String outTradeNo, BigDecimal totalAmount, String subject) throws Exception {
        // 请求支付宝下单接口
        AlipayTradePagePayResponse response = Factory.Payment.Page()
                .pay(
                        subject,           // 订单标题
                        outTradeNo,           // 商户订单号（唯一）
                        String.valueOf(totalAmount), // 金额（单位：元）
                        alipayConfig.getReturnUrl()                 // 可选：支付成功跳转URL（一般用异步通知）
                );
        return response.getBody();
    }

    /**
     * 查询订单支付状态
     */
    public String queryTradeStatus(String outTradeNo) throws Exception {
        AlipayTradeQueryResponse query = Factory.Payment.Common().query(outTradeNo);
        return query.tradeStatus;
    }
}
