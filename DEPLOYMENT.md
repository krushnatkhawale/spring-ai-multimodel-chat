# Deploying Spring AI Multimodel Chat to Render.com

This document outlines the steps to deploy your Spring Boot application, `spring-ai-multimodel-chat`, to Render.com using Docker and Render Blueprints.

## Prerequisites

1.  **Render Account**: You need an active account on [Render.com](https://render.com/).
2.  **Git Repository**: Your project must be hosted on a Git provider (e.g., GitHub, GitLab, Bitbucket).
3.  **Docker and Render Blueprint Files**:
    *   `Dockerfile`: Located in the root of your project, defining how your application is containerized.
    *   `render.yaml`: Located in the root of your project, defining your Render service(s) using Blueprints.

## Project Files for Deployment

You should have the following files in your project root:

*   **`Dockerfile`**:
    ```dockerfile
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
    ```
    This `Dockerfile` sets up a Java 17 environment, copies your Gradle project, builds a Spring Boot executable JAR, and then runs it.

*   **`render.yaml`**:
    ```yaml
    # This file defines the services for deploying your Spring Boot application to Render.com.
    # For more information, see https://render.com/docs/yaml-blueprint

    services:
      - type: web
        name: spring-ai-multimodel-chat
        env: docker
        # The buildCommand is empty because the Dockerfile handles the build process.
        buildCommand: ""
        # The startCommand is empty because the ENTRYPOINT in the Dockerfile handles starting the application.
        startCommand: ""
        # The port your Spring Boot application listens on.
        port: 8080
        # You can specify environment variables here if needed, e.g., for database connections or API keys.
        # envVars:
        #   - key: DATABASE_URL
        #     value: ${YOUR_DATABASE_URL}
        #   - key: API_KEY
        #     value: ${YOUR_API_KEY}
    ```
    This `render.yaml` file is a Render Blueprint that tells Render to deploy a web service named `spring-ai-multimodel-chat` using Docker. It specifies that the application listens on port `8080`.

## Deployment Steps

1.  **Commit and Push Your Code**:
    Ensure that your `Dockerfile` and `render.yaml` files are committed to your Git repository and pushed to your remote (e.g., GitHub).

2.  **Log in to Render**:
    Go to [Render.com](https://render.com/) and log in to your account.

3.  **Create a New Blueprint Instance**:
    *   In your Render dashboard, click on "New" -> "Blueprint Instance".
    *   Connect your Git repository where `spring-ai-multimodel-chat` is hosted.
    *   Render will automatically detect the `render.yaml` file in your repository.
    *   Review the proposed services (you should see `spring-ai-multimodel-chat` listed as a web service).
    *   Click "Apply" to create the services.

4.  **Monitor Deployment**:
    Render will now start building and deploying your application. You can monitor the progress in the Render dashboard under the "Logs" tab for your service.
    *   Render will first build the Docker image using your `Dockerfile`.
    *   Once the image is built, it will deploy the container.

5.  **Access Your Application**:
    Once the deployment is successful, Render will provide a public URL for your web service. You can find this URL on the service's dashboard page.

## Environment Variables

If your application requires environment variables (e.g., database connection strings, API keys), you can add them in two ways:

1.  **In `render.yaml`**: Uncomment and fill in the `envVars` section in your `render.yaml` file. This is suitable for non-sensitive variables or if you are using Render's secret management.
    ```yaml
    # ...
    envVars:
      - key: MY_APP_SETTING
        value: "some_value"
    ```
2.  **Via Render Dashboard**: For sensitive information, it's recommended to add environment variables directly in the Render dashboard:
    *   Navigate to your service in the Render dashboard.
    *   Go to the "Environment" tab.
    *   Add new environment variables with their respective keys and values. These will be securely injected into your running container.

By following these steps, your Spring Boot application will be deployed and running on Render.com.
