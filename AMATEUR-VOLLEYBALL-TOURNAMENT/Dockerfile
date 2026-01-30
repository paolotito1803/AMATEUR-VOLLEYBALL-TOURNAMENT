# Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Project configuration
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Source code
COPY src src

# Fix windows line endings and grant execute permissions
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Build jar
# -x test skips tests
RUN ./gradlew bootJar -x test --no-daemon

# Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from build
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]