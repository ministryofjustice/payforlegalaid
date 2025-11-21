# Multi-stage Dockerfile for local development
# Stage 1: Build the application and run tests
FROM maven:3.9-amazoncorretto-17 AS builder

WORKDIR /build

# Install git (required for cloning OpenAPI repository)
RUN yum install -y git && yum clean all

# Copy pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .
COPY build-openapi.sh .

# Make the build script executable
RUN chmod +x build-openapi.sh

# Build OpenAPI dependency
RUN ./build-openapi.sh

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application with tests
RUN mvn package

# Stage 2: Runtime
FROM amazoncorretto:17-alpine

RUN apk update && apk add --no-cache curl

WORKDIR /app

# Copy the application jar
COPY --from=builder /build/target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar ./app.jar

# Create application user
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup

# Create directory for H2 database with proper permissions
RUN mkdir -p /home/appuser && chown -R appuser:appgroup /home/appuser

USER 1001

# Expose the application port (using 8080 for local development)
EXPOSE 8080

# Set Spring profile to local
ENV SPRING_PROFILES_ACTIVE=local

# Run with local-friendly JVM settings
ENTRYPOINT ["java", \
    "-Xms512m", \
    "-Xmx1g", \
    "-Dspring.profiles.active=local", \
    "-jar", "app.jar"]
