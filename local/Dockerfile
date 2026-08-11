# ---------- Stage 1: Build ----------
FROM maven:3.9.14-eclipse-temurin-25@sha256:304ea1e9675d07612c36d5d70d8bd1dbd8aac64c307253bbde4e488857f90faa AS builder

WORKDIR /build

# Copy pom.xml first - dependency cache invalidates only when pom changes
COPY pom.xml .

# Build dependencies (use the cache if nothing has changed in pom, use local Maven settings to get glad-parent)
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn -s /root/.m2/settings.xml -B -q dependency:go-offline

# Copy source and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn -s /root/.m2/settings.xml -B clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:25-jre-alpine@sha256:c707c0d18cb9e8556380719f80d96a7529d0746fbb42143893949b98ed2f8943

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p /data && chown -R appuser:appgroup /data

COPY --from=builder /build/target/*exec.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]