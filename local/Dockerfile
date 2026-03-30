# ---------- Stage 1: Build ----------
FROM maven:3.9.14-eclipse-temurin-25 AS builder

WORKDIR /build

# Install git (needed for cloning openapi)
RUN apt-get update && apt-get install -y --no-install-recommends git && rm -rf /var/lib/apt/lists/*

# Copy pom.xml first - version extraction + dependency cache invalidates only when pom changes
COPY pom.xml .

# Build OpenAPI dependency (cached unless pom.xml changes)
RUN --mount=type=cache,target=/root/.m2 \
    OPENAPI_VERSION=$(mvn help:evaluate -Dexpression=payforlegalaid-openapi.version -q -DforceStdout) && \
    if [ -z "$OPENAPI_VERSION" ]; then echo "ERROR: Could not extract OpenAPI version" && exit 1; fi && \
    echo "OpenAPI version: $OPENAPI_VERSION" && \
    git clone --depth 1 --single-branch --branch "v$OPENAPI_VERSION" \
      https://github.com/ministryofjustice/payforlegalaid-openapi.git openapi && \
    cd openapi && \
    mvn -B clean install -DskipTests -Dmaven.javadoc.skip=true -Dmaven.source.skip=true

# Resolve main project dependencies (cached unless pom.xml changes)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

# Copy source and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p /data && chown -R appuser:appgroup /data

COPY --from=builder /build/target/*exec.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]