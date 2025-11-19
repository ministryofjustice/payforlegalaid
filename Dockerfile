# Dockerfile for Pay For Legal Aid application
# This expects the JAR to be built locally first
FROM amazoncorretto:17-alpine

RUN apk update && apk add --no-cache curl

WORKDIR /app

# Copy the pre-built JAR file
COPY target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar ./app.jar

# Copy pre-initialized database files if they exist
COPY target/docker-db-init/ ./data/

# Create non-root user
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup

# Create directories for H2 database and ensure proper permissions
RUN mkdir -p /app/data /app/logs && \
    chown -R appuser:appgroup /app

USER 1001

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-Xms1g", \
    "-Xmx2g", \
    "-XX:MaxRAMPercentage=75", \
    "-XX:NativeMemoryTracking=detail", \
    "-Doracle.jdbc.maxCachedBufferSize=524288", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=400", \
    "-XX:InitiatingHeapOccupancyPercent=35", \
    "-XX:+ExplicitGCInvokesConcurrent", \
    "-XX:+ParallelRefProcEnabled", \
    "-XX:+HeapDumpOnOutOfMemoryError", \
    "-XX:HeapDumpPath=/app/logs/heap-dump.hprof", \
    "-Dspring.profiles.active=local", \
    "-Dspring.datasource.url=jdbc:h2:file:/app/data/gpfd;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE", \
    "-jar", "app.jar"]
