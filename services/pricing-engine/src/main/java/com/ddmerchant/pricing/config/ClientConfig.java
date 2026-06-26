package com.ddmerchant.pricing.config;

import com.ddmerchant.pricing.client.DidiClient;
import com.ddmerchant.pricing.client.HttpDidiClient;
import com.ddmerchant.pricing.client.MockDidiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Value("${didi.api.mode:mock}")
    private String mode;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public DidiClient didiClient(WebClient.Builder webClientBuilder, DidiProperties props, MockDidiClient mockDidiClient) {
        if ("http".equalsIgnoreCase(props.getMode()) || "http".equalsIgnoreCase(mode)) {
            return new HttpDidiClient(webClientBuilder, props);
        }
        return mockDidiClient;
    }
}
