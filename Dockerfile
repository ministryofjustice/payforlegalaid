FROM amazoncorretto:17-alpine
RUN apk update && apk add --no-cache curl

WORKDIR /app
COPY target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar ./app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001

EXPOSE 8443 9010 9090

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
    "-Dcom.sun.management.jmxremote", \
    "-Dcom.sun.management.jmxremote.port=9010", \
    "-Dcom.sun.management.jmxremote.rmi.port=9010", \
    "-Dcom.sun.management.jmxremote.local.only=false", \
    "-Dcom.sun.management.jmxremote.authenticate=false", \
    "-Dcom.sun.management.jmxremote.ssl=false", \
    "-Djava.rmi.server.hostname=localhost", \
     "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9090", \
    "-jar", "app.jar"]