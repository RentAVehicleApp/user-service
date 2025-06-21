# Сборка
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY user-service-model/pom.xml user-service-model/
RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package -DskipTests

# Запуск
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]