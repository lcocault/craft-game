FROM eclipse-temurin:17-jre
WORKDIR /app

ARG APP_JAR=backend/target/backend-0.0.1-SNAPSHOT.jar
COPY ${APP_JAR} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
