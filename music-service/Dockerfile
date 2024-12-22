# Runtime
FROM openjdk:17-jdk-slim

# Working directory inside the container
WORKDIR /root/music-service

# Copy file builded by Maven
COPY target/*.jar authen-services-3.3.5.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "authen-services-3.3.5.jar"]