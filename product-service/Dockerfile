FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY common-lib ./common-lib
COPY product-service ./product-service
RUN mvn -B -q -f common-lib/pom.xml install -DskipTests
RUN mvn -B -q -f product-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/product-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
