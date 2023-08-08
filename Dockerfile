FROM bitnami/nginx:1.25.1
#FROM amazoncorretto:17-alpine
#FROM openjdk:11

#RUN mkdir -p /opt/laa-crime-applications-adaptor/
#WORKDIR /opt/laa-crime-applications-adaptor/
#COPY ./build/libs/crime-applications-adaptor.jar /opt/laa-crime-applications-adaptor/app.jar


WORKDIR /app
COPY ./src .
#RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
#USER 1001
EXPOSE 8088 8099
ENTRYPOINT ["java","-jar","app.jar"]