FROM openjdk:17-jdk-slim
VOLUME /tmp
VOLUME /logs
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]