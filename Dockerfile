FROM amazoncorretto:25-alpine
RUN apk update && apk add --no-cache curl

WORKDIR /app
COPY target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar ./app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8443
ENTRYPOINT ["java", \
    "-Xms2g", \
    "-Xmx4g", \
    "-XX:MaxRAMPercentage=85", \
    "-XX:NativeMemoryTracking=detail", \
    "-Doracle.jdbc.maxCachedBufferSize=524288", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=400", \
    "-XX:InitiatingHeapOccupancyPercent=35", \
    "-XX:+ExplicitGCInvokesConcurrent", \
    "-XX:+ParallelRefProcEnabled", \
    "-XX:+HeapDumpOnOutOfMemoryError", \
    "-XX:HeapDumpPath=/tmp/heap-dump.hprof", \
    "-jar", "app.jar"]
