FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Копируем pom файлы
COPY pom.xml .
COPY user-service-model/pom.xml user-service-model/
COPY user-service-app/pom.xml user-service-app/

# Загружаем зависимости
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY user-service-model user-service-model/
COPY user-service-app user-service-app/

# Собираем проект
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/user-service-app/target/*.jar app.jar

# Не нужно EXPOSE для Railway
# Используем shell форму для поддержки переменных
CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]