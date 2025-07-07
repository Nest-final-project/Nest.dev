# Dockerfile
# Base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from your local machine to the container
# Gradle project에서는 build/libs 디렉토리에 JAR 파일이 생성됩니다.
# 실제 JAR 파일명(예: nest.dev-0.0.1-SNAPSHOT.jar)으로 변경해야 합니다.
COPY build/libs/*.jar app.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]