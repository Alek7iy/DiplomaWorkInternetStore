# Этап 1: Сборка с использованием Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build


WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

RUN cp target/*.jar app.jar


# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -g 1000 -S spring && \
    adduser -u 1000 -S spring -G spring

USER spring

WORKDIR /home/spring

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
