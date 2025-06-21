FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
# используем JDK 17
FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]