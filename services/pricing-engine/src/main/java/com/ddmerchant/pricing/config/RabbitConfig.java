package com.ddmerchant.pricing.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String DIDI_WEBHOOK_QUEUE = "didi.webhook.events";

    @Bean
    public Queue didiWebhookQueue() {
        return new Queue(DIDI_WEBHOOK_QUEUE, true);
    }
}
