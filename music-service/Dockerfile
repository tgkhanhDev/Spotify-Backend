# Runtime
FROM eclipse-temurin:17-jre-alpine

# Working directory inside the container
WORKDIR /root/music-service

# Copy file builded by Maven
COPY target/*.jar music-service.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "music-service.jar"]