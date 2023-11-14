FROM amazoncorretto:17-alpine
RUN apk update && apk add --no-cache curl
WORKDIR /app
COPY target/pay-for-legal-aid-0.0.1-SNAPSHOT.jar ./app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

