# Runtime
FROM openjdk:17-jdk-slim

# Working directory inside the container
WORKDIR /root/auth-service

# Copy file builded by Maven
COPY target/*.jar auth-service.jar
COPY .env .

# Run the JAR file
ENTRYPOINT ["java", "-jar", "auth-service.jar"]