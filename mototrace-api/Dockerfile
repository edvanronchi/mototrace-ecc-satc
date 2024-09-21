FROM eclipse-temurin:21-jdk-alpine AS builder

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/mototrace-api-*.jar /app/mototrace-api.jar

ENTRYPOINT ["java", "-jar", "/app/mototrace-api.jar"]