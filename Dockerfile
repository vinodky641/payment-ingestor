FROM eclipse-temurin:25-jre

RUN useradd --system --uid 10001 --create-home appuser

WORKDIR /app

COPY --chown=10001:10001 target/*.jar app.jar

USER 10001

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
