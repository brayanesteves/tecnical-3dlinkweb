# Multi-stage build for Spring PetClinic Modernized Backend (Java 21)
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace/app

# Cache Maven dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copy source and build executable JAR
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-root user
RUN addgroup -S petclinic && adduser -S petclinic -G petclinic
USER petclinic

COPY --from=builder /workspace/app/target/spring-petclinic-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
