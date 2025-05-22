FROM eclipse-temurin:21-jdk-alpine

VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

RUN printf '#!/bin/sh\n\
echo "Esperando 30 segundos antes de iniciar..."\n\
sleep 30\n\
exec java -jar /app.jar\n' > /entrypoint.sh && chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]