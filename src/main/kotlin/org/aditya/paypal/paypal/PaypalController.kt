package org.aditya.paypal.paypal

import com.paypal.core.rest.PayPalRESTException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView


@Controller
class PaypalController(
    private val paypalService: PaypalService,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass);

    @GetMapping("/")
    fun home(): String {
        return "index";
    }

    @PostMapping("/payment/create")
    fun createPayment(
        @RequestParam("method") method: String,
        @RequestParam("amount") amount: String,
        @RequestParam("currency") currency: String,
        @RequestParam("description") description: String,
    ): RedirectView {
        try {
            val cancelUrl = "http://localhost:8080/payment/cancel";
            val successUrl = "http://localhost:8080/payment/success";
            val payment = paypalService.createPayment(
                total = amount.toDouble(),
                currency = currency,
                method = method,
                intent = "sale",
                description = description,
                cancelUrl = cancelUrl,
                successUrl = successUrl
            );
            for (links in payment.links) {
                if (links.rel == "approval_url") {
                    return RedirectView(links.href);
                }
            }
        } catch (e: PayPalRESTException) {
            logger.error("Error occurred:: ", e);
        }
        return RedirectView("/payment/error");
    }

    @GetMapping("/payment/success")
    fun paymentSuccess(
        @RequestParam("paymentId") paymentId: String,
        @RequestParam("PayerID") payerId: String
    ): String {
        try {
            val payment = paypalService.executePayment(paymentId, payerId);
            if (payment.state == "approved") {
                return "paymentSuccess";
            }
        } catch (e: PayPalRESTException) {
            logger.error("Error occured::", e);
        }
        return "paymentSuccess";
    }

    @GetMapping("/payment/cancel")
    fun paymentCancel(): String {
        return "paymentCancel";
    }

    @GetMapping("/payment/error")
    fun paymentError(): String {
        return "paymentError";
    }
}