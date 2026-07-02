# Spring Boot backend

This is a minimal Spring Boot application (Maven, Java 17) with a sample /api/health endpoint.

Run (requires a MySQL database or modify application.properties to use H2):

```bash
cd backend
mvn spring-boot:run
```

Default database connection (edit src/main/resources/application.properties):

spring.datasource.url=jdbc:mysql://localhost:3306/ddmerchant?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=changeme

The application will listen on port 8080 by default.
