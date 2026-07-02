# Didi integration configuration

This section describes environment variables used to configure the Didi client behavior.

- DIDI_API_MODE: mock | http (default: mock)
- DIDI_API_BASE_URL: base URL of Didi API (e.g. https://api.didi.com)
- DIDI_API_UPDATE_PATH: API path to update single price (default: /v1/price/update)
- DIDI_API_AUTH_TYPE: token | oauth2 | signature (default: token)
- DIDI_API_TOKEN: static token used when auth_type=token
- DIDI_OAUTH_TOKEN_URL: OAuth token endpoint (if auth_type=oauth2)
- DIDI_CLIENT_ID / DIDI_CLIENT_SECRET: OAuth client credentials
- DIDI_SIGNATURE_SECRET: secret used to compute HMAC signature when auth_type=signature
- DIDI_RATE_LIMIT_PER_SECOND: requests per second (default: 5)
- DIDI_RETRY_COUNT: number of retries for HTTP calls (default: 3)
- DIDI_BATCH_UPDATE_ENABLED: true|false (default: false)

How to switch to HTTP mode:

1. Set DIDI_API_MODE=http and provide at least DIDI_API_BASE_URL and DIDI_API_AUTH_TYPE plus credentials (e.g. DIDI_API_TOKEN).
2. In k8s / Docker, inject secrets via environment variables or use secret manager; never commit credentials to Git.

Note: The HttpDidiClient implementation uses a simple local token-bucket rate limiter and a basic retry loop with exponential backoff. This is intentionally simple; for production use consider resilience libraries (Resilience4j) and a proper credential/token refresh implementation.
