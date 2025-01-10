# Runtime
FROM eclipse-temurin:17-jre-alpine

# Working directory inside the container
WORKDIR /root/auth-service

# Copy file builded by Maven
COPY target/*.jar auth-service.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "auth-service.jar"]