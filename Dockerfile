# syntax=docker/dockerfile:1
FROM openjdk:18-alpine3.14
WORKDIR /app

# Install required packages
RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add nodejs npm git maven

# Copy application source code to the container
COPY . /app

# Build the application
RUN mvn clean install

# Set the command to change directory to target and run the application with the production profile
CMD ["sh", "-c", "cd target && java -jar spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"]

# Expose the application port
EXPOSE 8017
