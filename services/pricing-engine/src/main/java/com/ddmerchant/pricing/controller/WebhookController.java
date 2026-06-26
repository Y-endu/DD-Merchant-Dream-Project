package com.ddmerchant.pricing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook/didi")
public class WebhookController {

    @PostMapping("/event")
    public ResponseEntity<String> receiveEvent(@RequestBody Map<String,Object> payload) {
        // TODO: validate signature, push to queue
        System.out.println("Received Didi webhook: " + payload);
        return ResponseEntity.ok("accepted");
    }
}
