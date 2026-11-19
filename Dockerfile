# ==============================================================================
# AI-ENABLED HOSPITAL RESOURCE AND EMERGENCY TRIAGE SYSTEM
# Production Multi-Stage Dockerfile
# ==============================================================================

# Stage 1: Build Application with Maven and Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source and build jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Non-root security user
RUN addgroup --system hospital && adduser --system --ingroup hospital hospital
USER hospital:hospital

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
