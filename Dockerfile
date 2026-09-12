# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy the entire project
COPY . .

# Build the backend JAR
# Note: We use the root gradlew to build the subproject
RUN chmod +x gradlew
RUN ./gradlew clean :backend:bootJar --no-daemon

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/backend/build/libs/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application with optimized memory for 512MB RAM
ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-jar", "app.jar"]
