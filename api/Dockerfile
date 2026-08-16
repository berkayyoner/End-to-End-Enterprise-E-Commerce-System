FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY common-lib ./common-lib
COPY api ./api
RUN mvn -B -q -f common-lib/pom.xml install -DskipTests
RUN mvn -B -q -f api/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
