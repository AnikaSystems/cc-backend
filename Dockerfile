# Use a base image with Maven pre-installed
FROM maven:3.9.6-amazoncorretto-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file to the container
COPY pom.xml .

# Download dependencies and build the project
RUN mvn dependency:go-offline

# Copy the source code to the container
COPY src ./src

# Build the application
RUN mvn package

# Use a lightweight base image for the runtime environment
FROM amazoncorretto:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the previous stage to the runtime environment
COPY --from=build */target/case-management-service-0.0.1.jar ./app.jar

# Specify the command to run the application
CMD ["java", "-jar", "app.jar"]
