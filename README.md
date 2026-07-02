# DD Merchant Dream Project — Pricing Engine

This branch implements an initial pricing engine, DB schema, webhook receiver, and a Spring Boot service skeleton to support:

- Real-time webhook ingestion from Didi
- Competitor price aggregation storage
- A simple rule-based pricing engine (floor price, delta to competitor, percentage drop limits)
- Price history logging and a mock Didi client for price updates

How to run locally (basic):

1. Install Docker & Docker Compose
2. From repo root:
   docker-compose up --build

The docker-compose brings up Postgres, RabbitMQ and the pricing-engine service (built from services/pricing-engine).

Next steps: I will expand business logic, add unit tests, metrics and more endpoints. See the feature branch: feature/pricing-engine
