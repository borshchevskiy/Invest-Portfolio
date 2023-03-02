#
# Build stage
#
FROM maven:3.8.6-eclipse-temurin-17-alpine AS builder
COPY . .
RUN mvn clean package -DskipTests
#
# Package stage
#
FROM eclipse-temurin:17-jdk-alpine
COPY --from=builder /target/*.jar *.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "*.jar" ]

