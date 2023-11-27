#FROM amazoncorretto:17-alpine
#FROM eclipse-temurin:17.08-alpine
#Oracle base image is deprecated so i wont try that

FROM alpine:latest
#RUN apk add --no-cache openJdk17
RUN uname -m
RUN tar zxvf jdk-17_linux-x64_bin.tar.gz
RUN apk update && apk add --no-cache curl

#trying oracle java on top of vanilla basic alpine image:
ENV JAVA_HOME=/usr/lib/jvm/default-jvm
ENV PATH=${PATH}:${JAVA_HOME}/bin

WORKDIR /app
COPY target/pay-for-legal-aid-0.0.1-SNAPSHOT.jar ./app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

