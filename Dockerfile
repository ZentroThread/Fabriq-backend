# Use the Java 21 Alpine image (Lightweight)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file into the image
# Ensure your local build actually produced a jar in /target
COPY target/*.jar app.jar

# Expose the port Spring runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]