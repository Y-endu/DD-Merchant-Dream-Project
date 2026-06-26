FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -f pom.xml -DskipTests package
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/pricing-engine-0.0.1-SNAPSHOT.jar"]
