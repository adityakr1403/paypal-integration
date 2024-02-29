package org.aditya.paypal.paypal

import com.paypal.api.payments.Amount
import com.paypal.api.payments.Payer
import com.paypal.api.payments.Payment
import com.paypal.api.payments.PaymentExecution
import com.paypal.api.payments.RedirectUrls
import com.paypal.api.payments.Transaction
import com.paypal.base.rest.APIContext
import org.springframework.stereotype.Service
import java.util.*


@Service
class PaypalService(
    val apiContext: APIContext
) {

    fun createPayment(
        total: Double,
        currency: String,
        method: String,
        intent: String,
        description: String,
        cancelUrl: String,
        successUrl: String
    ): Payment {
        val amount = Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total)); // 9.99$ - 9,99€

        val transaction = Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        val transactions = mutableListOf<Transaction>();
        transactions.add(transaction);

        val payer = Payer();
        payer.setPaymentMethod(method);

        val payment = Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        val redirectUrls = RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    fun executePayment(paymentId: String, payerId: String): Payment {
        val payment = Payment();
        payment.setId(paymentId);

        val paymentExecution = PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }
}