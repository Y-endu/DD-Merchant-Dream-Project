package com.ddmerchant.pricing.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.ddmerchant.pricing.config.RabbitConfig;

@RestController
@RequestMapping("/webhook/didi")
public class WebhookController {

    private final RabbitTemplate rabbitTemplate;

    public WebhookController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/event")
    public ResponseEntity<String> receiveEvent(@RequestBody Map<String,Object> payload) {
        // TODO: validate signature
        System.out.println("Received Didi webhook (controller): " + payload);
        rabbitTemplate.convertAndSend(RabbitConfig.DIDI_WEBHOOK_QUEUE, payload);
        return ResponseEntity.ok("accepted");
    }
}
