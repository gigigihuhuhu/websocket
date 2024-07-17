# Use a Gradle image to build the project
FROM gradle:7.6.0-jdk17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle wrapper and the build script
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

# Use an OpenJDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/build/libs/websocket-server-*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Set the entry point to run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
