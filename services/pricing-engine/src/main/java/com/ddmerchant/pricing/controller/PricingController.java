package com.ddmerchant.pricing.controller;

import com.ddmerchant.pricing.service.PricingEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingEngineService pricingEngineService;

    public PricingController(PricingEngineService pricingEngineService) {
        this.pricingEngineService = pricingEngineService;
    }

    @PostMapping("/recalculate")
    public ResponseEntity<String> recalculate(@RequestParam(required = false) Long shopId,
                                              @RequestParam(required = false) Long modelId) {
        pricingEngineService.recalculate(shopId, modelId);
        return ResponseEntity.ok("recalculation started");
    }

    @PostMapping("/override")
    public ResponseEntity<String> override(@RequestBody Map<String,Object> body) {
        // body: vehicleId, newPrice, operator
        pricingEngineService.overridePrice(body);
        return ResponseEntity.ok("override applied");
    }
}
