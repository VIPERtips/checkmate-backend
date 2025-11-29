# First stage: Build with JDK 25
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy pom.xml first for caching dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the project without tests
RUN mvn clean package -DskipTests

# Second stage: Minimal runtime
FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
