FROM maven:3.8.1-openjdk-17-slim as builder
WORKDIR /builder
COPY pom.xml .
COPY .mvn/ .
COPY mvnw .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean install -DskipTests=true -Dspring_profiles_active=prod

FROM eclipse-temurin:17-jre-alpine
WORKDIR /application
EXPOSE 8080
COPY --from=builder /builder/target/*.jar ./*.jar
ENTRYPOINT ["java", "-jar", "*.jar" ]
