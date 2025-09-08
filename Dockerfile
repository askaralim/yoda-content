# Multi-stage build for yoda-content microservice
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install necessary packages for production
RUN apk add --no-cache wget curl

# Create app user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /yoda-content

# Copy the built application from builder stage
COPY build/libs/yoda-content-0.0.1-SNAPSHOT.jar yoda-content.jar

# Create necessary directories
RUN mkdir -p /yoda-content/logs && \
    chown -R appuser:appgroup /yoda-content

# Expose port
EXPOSE 8082

# Set timezone
ENV TZ=Asia/Shanghai

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -Djava.security.egd=file:/dev/./urandom"

# Run the application with optimized JVM settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=docker -jar yoda-content.jar"]

