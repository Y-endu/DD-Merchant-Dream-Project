# Didi properties binding
package com.ddmerchant.pricing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DidiProperties {

    @Value("${didi.api.mode:mock}")
    private String mode;

    @Value("${didi.api.base-url:}")
    private String baseUrl;

    @Value("${didi.api.update-path:/v1/price/update}")
    private String updatePath;

    @Value("${didi.api.auth-type:token}")
    private String authType;

    @Value("${didi.api.token:}")
    private String apiToken;

    @Value("${didi.api.oauth.token-url:}")
    private String oauthTokenUrl;

    @Value("${didi.api.client-id:}")
    private String clientId;

    @Value("${didi.api.client-secret:}")
    private String clientSecret;

    @Value("${didi.api.signature-secret:}")
    private String signatureSecret;

    @Value("${didi.api.rate-limit-per-second:5}")
    private int rateLimitPerSecond;

    @Value("${didi.api.retry-count:3}")
    private int retryCount;

    @Value("${didi.api.batch-update-enabled:false}")
    private boolean batchUpdateEnabled;

    public String getMode() { return mode; }
    public String getBaseUrl() { return baseUrl; }
    public String getUpdatePath() { return updatePath; }
    public String getAuthType() { return authType; }
    public String getApiToken() { return apiToken; }
    public String getOauthTokenUrl() { return oauthTokenUrl; }
    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getSignatureSecret() { return signatureSecret; }
    public int getRateLimitPerSecond() { return rateLimitPerSecond; }
    public int getRetryCount() { return retryCount; }
    public boolean isBatchUpdateEnabled() { return batchUpdateEnabled; }
}
