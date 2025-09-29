// payment/src/main/java/com/self/appreciation/payment/controller/AlipayCallbackController.java
package com.self.appreciation.payment.controller;

import com.alipay.easysdk.factory.Factory;
import com.self.appreciation.payment.config.AlipayConfig;
import com.self.appreciation.payment.entity.Payment;
import com.self.appreciation.payment.repository.PaymentRepository;
import com.self.appreciation.payment.util.AlipayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alipay")
public class AlipayCallbackController {

    private final AlipayConfig alipayConfig;
    private final PaymentRepository paymentRepository;
    private final AlipayUtil alipayUtil;

    /**
     * 支付宝异步通知回调
     */
    @PostMapping("/notify")
    @Transactional
    public String alipayNotify(@RequestParam Map<String, String> params) {
        log.info("Received Alipay notify: {}", params);
        try {
            // 验证签名
            boolean verifyResult = Factory.Payment.Common().verifyNotify(params);


            if (verifyResult) {
                // 验签成功
                String tradeStatus = params.get("trade_status");
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");

                // 根据商户订单号查询支付记录
                Payment payment = paymentRepository.findByOutTradeNo(outTradeNo);
                if (payment != null) {
                    if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                        // 支付成功
                        payment.setStatus("SUCCESS");
                        payment.setTransactionId(tradeNo);
                        paymentRepository.save(payment);
                        log.info("支付宝支付成功，商户订单号：{}，支付宝交易号：{}", outTradeNo, tradeNo);
                    } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                        // 交易关闭
                        payment.setStatus("FAILED");
                        paymentRepository.save(payment);
                        log.info("支付宝交易关闭，商户订单号：{}", outTradeNo);
                    }
                }
                // 返回成功标识给支付宝
                return "success";
            } else {
                log.warn("支付宝异步通知验签失败");
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付宝异步通知异常", e);
            return "fail";
        }
    }

    /**
     * 支付宝同步跳转回调
     */
    @GetMapping("/return")
    public String alipayReturn(@RequestParam Map<String, String> params) {
        try {
            // 验证签名
            boolean verifyResult = Factory.Payment.Common().verifyNotify(params);

            if (verifyResult) {
                String tradeStatus = params.get("trade_status");
                String outTradeNo = params.get("out_trade_no");

                // 主动查询订单支付结果
                tradeStatus = alipayUtil.queryTradeStatus(outTradeNo);

                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    // 支付成功，重定向到成功页面
                    return "订单：" + outTradeNo + "支付成功";
                } else {
                    return "订单：" + outTradeNo + "支付结果确认中";
                }
            }
        } catch (Exception e) {
            log.error("处理支付宝同步回调异常", e);
        }

        // 支付失败，重定向到失败页面
        return "verifying failure.";
    }

    /**
     * 支付宝同步跳转回调
     */
    @GetMapping("/query/{orderId}")
    public String alipayReturn(@PathVariable("orderId") Long orderId) {
        try {
            Payment byOrderId = paymentRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
            // 验证签名
            return alipayUtil.queryTradeStatus(byOrderId.getOutTradeNo());
        } catch (Exception e) {
            log.error("查询支付宝订单支付状态异常", e);
        }

        // 支付失败，重定向到失败页面
        return "query order tradeStatus failure.";
    }
}
