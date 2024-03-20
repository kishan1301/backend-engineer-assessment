# Stage 1: Build the Java application
FROM gradle:latest as gradle

RUN gradle init
RUN gradle build

# Stage 5: Deploy the Java application
FROM openjdk:21 AS java

#ENV JVM_RESOURCES="-Xmx500m -Xms500m"

# Copy the built JAR file from the previous stage
COPY build/libs/app-0.0.1-SNAPSHOT.jar /opt/
#
#COPY target/backend-engineer-assessment-0.0.1-SNAPSHOT.jar /opt/
#
# Expose the port
EXPOSE 8080
#
# Run the Java application
##CMD ["java", "-jar", "/opt/backend-engineer-assessment-0.0.1-SNAPSHOT.jar"]
WORKDIR /opt/

ADD https://github.com/vishnubob/wait-for-it/raw/master/wait-for-it.sh /usr/local/bin/wait-for-it
RUN chmod +x /usr/local/bin/wait-for-it

CMD ["wait-for-it", "postgres:5432", "temporal:7233", "--", "java", "-jar", "/opt/app-0.0.1-SNAPSHOT.jar"]
