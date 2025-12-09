# --------------------
# STAGE 1: BUILD STAGE (Compiles the Java code and creates the JAR)
# --------------------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine


# STAGE 2: Run stage
WORKDIR /app

# The JAR file is copied from the build stage (AS builder)
# We copy the specific JAR file created in the build stage
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]