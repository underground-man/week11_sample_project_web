FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

COPY src ./src
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring \
    && mkdir -p /app/uploads \
    && chown -R spring:spring /app

COPY --from=builder --chown=spring:spring /app/build/libs/week11-attachment-board-0.0.1-SNAPSHOT.jar app.jar

USER spring

ENV UPLOAD_DIR=/app/uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
