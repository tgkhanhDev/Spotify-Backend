# Runtime
FROM openjdk:17-jdk-slim

# Working directory inside the container
WORKDIR /root/api-gateway

# Copy file builded by Maven
COPY target/*.jar api-gateway.jar

EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]