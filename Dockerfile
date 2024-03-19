# Stage 1: Pull the Gradle image
FROM gradle:latest as gradle

# Stage 2: Start Temporal service
FROM temporalio/auto-setup:1.11.1 as temporal

# Start the Temporal service
CMD ["temporal", "server", "--root", "/etc/temporal", "--db", "cassandra", "start"]

# Stage 3: Start PostgreSQL service
FROM postgres:latest as postgres

# Set up environment variables
ENV POSTGRES_DB=mydatabase
ENV POSTGRES_PASSWORD=secret
ENV POSTGRES_USER=myuser

# Expose the PostgreSQL port
EXPOSE 5432

# Start the PostgreSQL service
CMD ["postgres"]

# Stage 4: Build the Java application
FROM gradle as builder

WORKDIR /opt/

COPY build.gradle /opt/

# Build and package the Java application
RUN gradle build -x test

# Stage 5: Deploy the Java application
FROM adoptopenjdk/openjdk21:jre-21.0.0_3-alpine

# Copy the built JAR file from the previous stage
#COPY --from=builder /app/build/libs/my-application.jar /app/my-application.jar

COPY target/backend-engineer-assessment-0.0.1-SNAPSHOT.jar /opt/

# Expose the port
EXPOSE 8080

# Run the Java application
CMD ["java", "-jar", "/opt/backend-engineer-assessment-0.0.1-SNAPSHOT.jar"]