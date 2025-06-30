# Dockerfile
# Base image

# 🌐 Step 1: 프론트엔드 빌드
FROM node:18 AS frontend-builder
WORKDIR /app/frontend
COPY ../Nest_frontend/frontend/ .
RUN npm install && npm run build


FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from your local machine to the container
# Gradle project에서는 build/libs 디렉토리에 JAR 파일이 생성됩니다.
# 실제 JAR 파일명(예: nest.dev-0.0.1-SNAPSHOT.jar)으로 변경해야 합니다.
COPY build/libs/*.jar app.jar

# ✅ 프론트 빌드 결과물을 백엔드 정적 자원 폴더로 복사
COPY --from=frontend-builder /app/frontend/dist /app/src/main/resources/static

# Expose the port your Spring Boot application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]