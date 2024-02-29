package org.aditya.paypal.config

import com.paypal.base.rest.APIContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PaypalConfig(
    @Value("\${paypal.client-id}") private var clientId: String,
    @Value("\${paypal.client-secret}") private var clientSecret: String,
    @Value("\${paypal.mode}") private var mode: String
) {


    @Bean
    fun apiContext(): APIContext {
        return APIContext(clientId, clientSecret, mode);
    }

}