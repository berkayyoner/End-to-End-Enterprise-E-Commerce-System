# Stage 1: Build the code directly inside Docker using the Maven image
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Step 1: Copy and install the shared common-lib first
COPY common-lib /app/common-lib
RUN cd common-lib && mvn clean install -DskipTests

# Step 2: Copy and build the order-service
COPY order-service/pom.xml /app/order-service/
COPY order-service/src /app/order-service/src
RUN cd order-service && mvn clean package -DskipTests

# Stage 2: Lightweight image to run the compiled, up-to-date JAR file
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Get the brand new JAR file generated in the previous builder stage
COPY --from=builder /app/order-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]