# Runtime
FROM eclipse-temurin:17-jre-alpine

# Working directory inside the container
WORKDIR /root/media-service

# Copy file builded by Maven
COPY target/*.jar media-service.jar
COPY .env .

# Run the JAR file
ENTRYPOINT ["java", "-jar", "media-service.jar"]