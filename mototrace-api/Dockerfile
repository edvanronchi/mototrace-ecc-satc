FROM openjdk:22-jdk-slim

WORKDIR /app

COPY target/mototrace-api-*.jar /app/

ENTRYPOINT ["java", "-jar", "/app/mototrace-api-*.jar"]
