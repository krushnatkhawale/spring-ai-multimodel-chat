# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle/
COPY build.gradle settings.gradle ./

# Grant execute permission to the Gradle wrapper
RUN chmod +x gradlew

# --- DIAGNOSTIC STEP ---
# List contents of the gradle directory to verify gradle-wrapper.jar is present
RUN ls -lR gradle
# -----------------------

# Copy source code
COPY src src/

# Build the application
# Use --no-daemon to avoid issues with daemon processes in Docker builds
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]