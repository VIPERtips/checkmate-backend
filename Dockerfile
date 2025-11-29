# First stage: Build
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy pom.xml first to download dependencies offline
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the project without tests
RUN mvn clean package -DskipTests

# Second stage: Run
FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app listens on
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
