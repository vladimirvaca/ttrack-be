# --- STAGE 1: Build ---
FROM gradle:8.11.1-jdk21-alpine AS build
WORKDIR /app
ENV CI=true

COPY . .
# Build the optimized Docker layers
RUN ./gradlew optimizedBuildLayers --no-daemon

# --- STAGE 2: Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the layers prepared by the Micronaut plugin (optimized build)
COPY --from=build /app/build/docker/optimized/layers/libs /app/libs
COPY --from=build /app/build/docker/optimized/layers/app/application.jar /app/application.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Dmicronaut.environments=prod", \
            "-jar", "/app/application.jar"]
