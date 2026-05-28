# Use a base image with Java 17
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle gradle

# Copy the build.gradle and settings.gradle files
COPY build.gradle settings.gradle ./

# Copy the source code
COPY src src

# Grant execute permission to the Gradle wrapper
RUN chmod +x gradlew

# Build the application
# Use --no-daemon to avoid issues with daemon processes in Docker builds
RUN ./gradlew bootJar --no-daemon

# Expose the port Spring Boot runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "build/libs/*.jar"]
