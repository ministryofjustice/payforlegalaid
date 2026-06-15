FROM amazoncorretto:25-alpine@sha256:80667e38af71ac103a3ae36a0b531d54c73c4da28fc02b57f69bce8993c0e1b0
RUN apk update && apk add --no-cache curl
# Stage 1: Dependency Resolution
FROM maven:3.9.13-amazoncorretto-25-alpine AS dependency-builder

# We pass in a branch ref, but default to main
ARG REPO_REF=main
ARG REPO_REF_OPEN_API=v0.0.36

WORKDIR /build-deps

# HIGH LEVEL: This step is about checking out the code
# apk add is installing software packages in alpine (the image of linux being used)
# No-cache so package index isn't stored => smaller image size
# Note: We don't actually remove the virtual package .build-deps at the end of each stage, because the new stage means they won't be copied into the final image anyway
RUN apk add --no-cache --virtual .build-deps \
        git \
        curl \
        gettext && \
    mkdir -p /build-deps  && \
    git config --global advice.detachedHead false && \
    git config --global http.sslVerify true && \
    git config --global gc.auto 0 && \
    git clone \
    --depth 1 \
    --branch "${REPO_REF_OPEN_API}" \
    --single-branch \
    --filter=blob:none \
    https://github.com/ministryofjustice/payforlegalaid-openapi && \
    rm -rf /build-deps/payforlegalaid-openapi/.git && \
    find /build-deps/payforlegalaid-openapi -type f -exec chmod 644 {} \;


RUN apk add --no-cache --virtual .build-deps \
        git \
        curl \
        gettext && \
    mkdir -p /build-deps  && \
    git config --global advice.detachedHead false && \
    git config --global http.sslVerify true && \
    git config --global gc.auto 0 && \
    git clone \
    --depth 1 \
    --branch "${REPO_REF}" \
    --single-branch \
    --filter=blob:none \
    https://github.com/ministryofjustice/payforlegalaid.git && \
    rm -rf /build-deps/payforlegalaid/.git && \
    find /build-deps/payforlegalaid -type f -exec chmod 644 {} \;

WORKDIR /build-deps/payforlegalaid-openapi

RUN mkdir -p /home/builder/.m2 && \
    mkdir -p /home/builder/.m2/repository && \
    chmod -R 775 /home/builder/.m2 && \
    mvn clean install -DfinalName=payforlegalaid-openapi-0.0.36 \
    -DskipTests \
    -Dmaven.artifact.threads=5

WORKDIR /build-deps/payforlegalaid

RUN mkdir -p /home/builder/.m2 && \
    mkdir -p /home/builder/.m2/repository && \
    chmod -R 775 /home/builder/.m2 && \
    mvn -B -e -X clean install \
    -DskipTests \
    -Dmaven.artifact.threads=5

FROM maven:3.9.13-amazoncorretto-25-alpine AS builder

# Stage 2: Building the code
# The idea here is basically that if the dependencies (i.e. payforlegalaid code) doesn't change, docker will reuse that stage of the image
# and only rebuild this stage

WORKDIR /build
COPY --from=dependency-builder --chown=root:root /root/.m2/repository /root/.m2/repository
COPY pom.xml src/ ./

RUN apk add --no-cache --virtual .build-deps gettext && \
    if [ -d "src/it/java" ]; then \
        mkdir -p src/main/java && \
        mv src/it/java/* src/main/java/ && \
        rmdir src/it/java; \
    fi && \
    if [ -d "src/it/resources" ]; then \
        mkdir -p src/main/resources && \
        mv src/it/resources/* src/main/resources/; \
    fi && \
    mkdir -p /build-artifacts/target && \
    mvn -B \
        -Dmaven.repo.local=/root/.m2/repository \
        -Pdev \
        clean package


# Stage 3: the final image, using a distroless image which contains just java and jvm - no shell, package manager etc
# So this is smaller, more secure, etc than using say alpine-coretto here like we did in Stage 1 and 2.

FROM gcr.io/distroless/java25-debian13
WORKDIR /app

LABEL org.opencontainers.image.authors="GPFD team (laa-payments-finance@digital.justice.gov.uk)" \
     org.opencontainers.image.description="Pay for Legal Aid Application" \
     org.opencontainers.image.vendor="Ministry of Justice" \
     org.opencontainers.image.title="Get Payments & Finance Data" \
     org.opencontainers.image.url="https://github.com/ministryofjustice/payforlegalaid" \
     org.opencontainers.image.documentation="https://github.com/ministryofjustice/payforlegalaid/readme.md" \
     org.opencontainers.image.source="https://github.com/ministryofjustice/payforlegalaid" \
     org.opencontainers.image.licenses="MIT" \
     org.opencontainers.image.base.name="gcr.io/distroless/java25-debian13"

# Copy the jar we built in Stage 2 to our current image.
# the 65532 is the UID for the nonroot user on the distroless image. so this chown ensures the image can run the jar as needed
COPY --from=builder --chown=65532:65532 /build/target/pay-for-legal-aid-*.jar app.jar

# Tells it to set the default user to the nonroot user
USER 65532:65532

# This lets the container be run as an executable - if you start the container it will run this command
COPY target/pay-for-legal-aid-*-exec.jar ./app.jar
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
    "-Dspring.profiles.active=dev", \
    "-jar", "app.jar"]
