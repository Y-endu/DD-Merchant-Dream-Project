# DD-Merchant-Dream-Project

滴滴商家梦想功能

This repository previously contained only a README. I added a minimal scaffold for a Spring Boot (Maven, Java 17) backend and a Vue 3 + Vite frontend on branch feature/springboot-vue-scaffold.

What I added

- backend/: Spring Boot application with a sample /api/health endpoint. Uses MySQL by default (edit backend/src/main/resources/application.properties). Java 17, Maven.
- frontend/: Vue 3 + Vite app (JavaScript) which calls the backend /api/health endpoint. Vite dev server proxies /api to the backend.

How to run locally (quickstart)

1) Start the backend (requires MySQL or change to H2 in application.properties):

```bash
cd backend
mvn spring-boot:run
```

2) Start the frontend:

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

Notes

- I created these files on branch feature/springboot-vue-scaffold. You can open a pull request from that branch to merge into main.
- If you prefer an in-memory DB for quick testing, switch application.properties to use H2 (example lines commented in the file).
