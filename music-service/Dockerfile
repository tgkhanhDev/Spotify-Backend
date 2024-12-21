# Runtime
FROM openjdk:17-jdk-slim

# Working directory inside the container
WORKDIR /root/authen-services

# Copy file builded by Maven
COPY target/*.jar authen-services.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "authen-services.jar"]