FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache bash curl

ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["/wait-for-it.sh", "rabbitmq:9100", "-t", "45", "--", "java", "-jar", "/app.jar"]
