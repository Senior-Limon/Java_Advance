package com.inno.task.payment_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient paymentGatewayClient(
            @Value("${app.payment-gateway.url:http://localhost:9999}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }
}