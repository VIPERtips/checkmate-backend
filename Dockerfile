# First stage: Build with Maven + JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Second stage: Lightweight runtime with JRE 21 Alpine
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
