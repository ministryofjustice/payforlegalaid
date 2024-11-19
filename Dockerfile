FROM amazoncorretto:17-alpine
RUN apk update && apk add --no-cache curl

WORKDIR /app
COPY /tmp/compiledJar.jar ./app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8443
ENTRYPOINT ["java","-jar","app.jar"]

