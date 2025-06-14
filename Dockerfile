FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/ToDo-Service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app.jar"]