# Use a base image with Java 17
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper files and build configuration
COPY gradlew .
COPY gradle gradle/
COPY build.gradle settings.gradle ./

# Grant execute permission to the Gradle wrapper
RUN chmod +x gradlew

# Download dependencies and ensure Gradle wrapper is initialized
# This step leverages Docker caching and ensures the wrapper is ready
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Expose the port Spring Boot runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "build/libs/*.jar"]
