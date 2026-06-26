package com.ddmerchant.pricing.listener;

import com.ddmerchant.pricing.config.RabbitConfig;
import com.ddmerchant.pricing.service.PricingEngineService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebhookListener {

    private final PricingEngineService pricingEngineService;

    public WebhookListener(PricingEngineService pricingEngineService) {
        this.pricingEngineService = pricingEngineService;
    }

    @RabbitListener(queues = RabbitConfig.DIDI_WEBHOOK_QUEUE)
    public void handleWebhook(Map<String,Object> payload) {
        // Example payload may contain shopId/modelId/order info
        System.out.println("[WebhookListener] received payload: " + payload);
        Object shopId = payload.get("shopId");
        Object modelId = payload.get("modelId");
        Long shop = shopId == null ? null : Long.valueOf(String.valueOf(shopId));
        Long model = modelId == null ? null : Long.valueOf(String.valueOf(modelId));
        pricingEngineService.recalculate(shop, model);
    }
}
