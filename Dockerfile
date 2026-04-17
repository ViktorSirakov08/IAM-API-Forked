# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Optimization: Copy pom.xml and download dependencies FIRST
# This allows Docker to cache the dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Security: Create a non-root user
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser

# Copy only the final executable jar
COPY --from=builder /build/target/*.jar app.jar

# Standard Spring Boot port (verify if 8081 is your actual server.port)
EXPOSE 8081

# Recommended: Use exec form and optimize JVM memory for containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]