# Builder Image
FROM eclipse-temurin:25-jdk-alpine AS jdk

# Required for strip-debug to work
RUN apk add --no-cache binutils

# Build JRE image with all modules
RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --modules-path ${JAVA_HOME}/jmods \
    --add-modules java.base,java.net.http,java.sql,java.naming,java.management,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=zip-6 \
    --output /full-jre

# Remove legal docs and unuseful binaries
RUN rm -rf /full-jre/legal &&\
    find /full-jre/bin -type f \
         ! -name java \
         ! -name jcmd \
         -delete

# Runtime image: 3 MB
FROM alpine:3.22

# Set up HTTP Health Check
RUN apk add --no-cache curl
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost/health || exit 1

# Include stripped JRE and fat JAR
COPY --from=jdk /full-jre /jre
COPY build/libs/hosted-all.jar app.jar

# Configure startup
ENV PORT=80
ENTRYPOINT [ "/jre/bin/java" ]
CMD [ "-jar", "app.jar"]

EXPOSE 80