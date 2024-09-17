FROM openjdk:22-jdk-slim

WORKDIR /app

COPY target/gateway-api-*.jar /app/

ENTRYPOINT ["java", "-jar", "/app/gateway-api-*.jar"]
