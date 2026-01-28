# ---- Build Stage (optional, multi-stage for smaller image) ----
FROM gradle:8.3.3-jdk17-alpine AS build
WORKDIR /app

# Copy only Gradle build files to leverage caching
COPY build.gradle settings.gradle ./
COPY src ./src

# Build the jar
RUN gradle bootJar --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the Spring Boot port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]