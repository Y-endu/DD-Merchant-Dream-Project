package com.ddmerchant.pricing.client;

import com.ddmerchant.pricing.config.DidiProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@Component
public class HttpDidiClient implements DidiClient {

    private final WebClient webClient;
    private final DidiProperties props;
    private final AtomicInteger tokens = new AtomicInteger(0);
    private final int capacity;
    private final ScheduledExecutorService refillScheduler = Executors.newSingleThreadScheduledExecutor();

    public HttpDidiClient(WebClient.Builder webClientBuilder, DidiProperties props) {
        this.props = props;
        this.capacity = Math.max(1, props.getRateLimitPerSecond());
        this.webClient = webClientBuilder.baseUrl(props.getBaseUrl()).build();
        // initialize tokens
        tokens.set(this.capacity);
        // refill every 1 second
        refillScheduler.scheduleAtFixedRate(() -> tokens.set(this.capacity), 1,1, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public boolean updatePrice(String externalVehicleId, double newPrice) {
        if (props.getBaseUrl() == null || props.getBaseUrl().isEmpty()) {
            System.err.println("HttpDidiClient: baseUrl not configured, aborting.");
            return false;
        }
        // simple token bucket rate limiting
        while (true) {
            int t = tokens.get();
            if (t > 0 && tokens.compareAndSet(t, t-1)) break;
            try { sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return false; }
        }

        String path = props.getUpdatePath();
        String url = path; // WebClient baseUrl already set
        Map<String,Object> payload = Map.of("externalVehicleId", externalVehicleId, "price", newPrice);

        int attempts = 0;
        int maxAttempts = Math.max(1, props.getRetryCount());
        long backoff = 500L;
        while (attempts < maxAttempts) {
            attempts++;
            try {
                WebClient.RequestBodySpec req = webClient.post()
                        .uri(url)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                if ("token".equalsIgnoreCase(props.getAuthType()) && props.getApiToken() != null && !props.getApiToken().isEmpty()) {
                    req.header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiToken());
                } else if ("signature".equalsIgnoreCase(props.getAuthType()) && props.getSignatureSecret() != null) {
                    // compute simple HMAC-SHA256 of payload string
                    String sig = HmacSha256.sign(payload.toString(), props.getSignatureSecret());
                    req.header("X-Signature", sig);
                } else if ("oauth2".equalsIgnoreCase(props.getAuthType())) {
                    // TODO: implement oauth2 token fetch/refresh. For now assume apiToken holds valid token
                    if (props.getApiToken() != null && !props.getApiToken().isEmpty()) {
                        req.header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiToken());
                    }
                }

                ClientResponse resp = req.bodyValue(payload)
                        .retrieve()
                        .toBodilessEntity()
                        .block(Duration.ofSeconds(5));

                if (resp != null && (resp.statusCode().is2xxSuccessful() || resp.statusCode().is3xxRedirection())) {
                    return true;
                } else {
                    System.err.println("HttpDidiClient: non-2xx response: " + (resp==null?"null":resp.statusCode()));
                }
            } catch (Exception ex) {
                System.err.println("HttpDidiClient: attempt " + attempts + " failed: " + ex.getMessage());
            }
            try { Thread.sleep(backoff); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return false; }
            backoff *= 2;
        }
        return false;
    }

    @PreDestroy
    public void shutdown() {
        refillScheduler.shutdownNow();
    }

    // inner helper for HMAC
    static class HmacSha256 {
        static String sign(String data, String secret) {
            try {
                javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
                mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
                // hex
                StringBuilder sb = new StringBuilder();
                for (byte b : sig) sb.append(String.format("%02x", b));
                return sb.toString();
            } catch (Exception e) {
                return "";
            }
        }
    }
}
