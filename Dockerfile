#
# Build stage
#
FROM maven:3.8.1-openjdk-17-slim AS builder
COPY . .
RUN mvn clean package -Pprod -DskipTests
#
# Package stage
#
FROM eclipse-temurin:17-jre-alpine
WORKDIR /application
EXPOSE 8080
COPY --from=builder /target/*.jar ./*.jar
ENTRYPOINT ["java", "-jar", "*.jar" ]