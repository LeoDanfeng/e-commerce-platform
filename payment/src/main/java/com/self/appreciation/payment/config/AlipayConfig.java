// payment/src/main/java/com/self/appreciation/payment/config/AlipayConfig.java
package com.self.appreciation.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId;
    private String privateKey;
    private String alipayPublicKey;
    private String protocol;
    private String gatewayHost;
    private String charset = "UTF-8";
    private String signType = "RSA2";
    private String format = "json";
    private String notifyUrl;
    private String returnUrl;
}
