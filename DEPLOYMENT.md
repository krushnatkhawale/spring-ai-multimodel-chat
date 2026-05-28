# Deploying Spring AI Multimodel Chat to Render.com

This document outlines the steps to deploy your Spring Boot application, `spring-ai-multimodel-chat`, to Render.com as a Web Service using your `Dockerfile`. This method is generally available on Render's free tier for basic deployments.

## Prerequisites

1.  **Render Account**: You need an active account on [Render.com](https://render.com/).
2.  **Git Repository**: Your project must be hosted on a Git provider (e.g., GitHub, GitLab, Bitbucket).
3.  **Dockerfile**: Located in the root of your project, defining how your application is containerized.

## Project Files for Deployment

You should have the following file in your project root:

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
    This `Dockerfile` sets up a Java 17 environment, copies your Gradle project, builds a Spring Boot executable JAR, and then runs it. Render will use this `Dockerfile` to build your application image.

## Deployment Steps (Web Service)

1.  **Commit and Push Your Code**:
    Ensure that your `Dockerfile` is committed to your Git repository and pushed to your remote (e.g., GitHub).

2.  **Log in to Render**:
    Go to [Render.com](https://render.com/) and log in to your account.

3.  **Create a New Web Service**:
    *   In your Render dashboard, click on "New" -> "Web Service".
    *   Connect your Git repository where `spring-ai-multimodel-chat` is hosted.
    *   Select the branch you want to deploy (e.g., `main` or `master`).
    *   Render will automatically detect your `Dockerfile` in the root of your repository.

4.  **Configure Your Web Service**:
    *   **Name**: Give your service a unique name (e.g., `spring-ai-multimodel-chat`).
    *   **Region**: Choose a region close to your users.
    *   **Branch**: Confirm the branch you want to deploy from.
    *   **Root Directory**: Leave empty if your `Dockerfile` is in the root.
    *   **Runtime**: Select `Docker`. Render should automatically detect this.
    *   **Build Command**: Leave this empty. Render will use your `Dockerfile` to build the image.
    *   **Start Command**: Leave this empty. The `ENTRYPOINT` in your `Dockerfile` handles starting the application.
    *   **Port**: Set this to `8080`, as specified in your `Dockerfile`'s `EXPOSE` instruction.
    *   **Plan**: Choose a plan. The "Free" plan is usually sufficient for testing and small projects, but check Render's current free tier limitations.

5.  **Create Web Service**:
    Click "Create Web Service".

6.  **Monitor Deployment**:
    Render will now start building and deploying your application. You can monitor the progress in the Render dashboard under the "Logs" tab for your service.
    *   Render will first build the Docker image using your `Dockerfile`.
    *   Once the image is built, it will deploy the container.

7.  **Access Your Application**:
    Once the deployment is successful, Render will provide a public URL for your web service. You can find this URL on the service's dashboard page.

## Environment Variables

If your application requires environment variables (e.g., database connection strings, API keys), you can add them directly in the Render dashboard:

*   Navigate to your service in the Render dashboard.
*   Go to the "Environment" tab.
*   Add new environment variables with their respective keys and values. These will be securely injected into your running container.

By following these steps, your Spring Boot application will be deployed and running on Render.com as a Web Service.