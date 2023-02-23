#
# Build Stage
#
FROM maven:3.8.6-openjdk-18-slim AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Packaging Stage
# Use an openjdk base image
FROM openjdk:18-jdk-slim

# Copy the compiled jar file from the build stage
COPY --from=build /target/*.jar app.jar

EXPOSE 8080
# Set the default command to run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]