FROM openjdk:22-jdk-slim

WORKDIR /app

COPY target/worker-position*.jar /app/

ENTRYPOINT ["java", "-jar", "/app/worker-position*.jar"]
