FROM adoptopenjdk/openjdk11:alpine-slim as lms-base-env
VOLUME /tmp

FROM lms-base-env as lms-timezone-env
RUN apk add --update tzdata
ENV TZ=Africa/Dar_es_Salaam

FROM lms-timezone-env as lms-target-files-env
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

FROM lms-target-files-env as lms-entrypoint-env
ENTRYPOINT  java $JAVA_OPTS -Djava.awt.headless=true -Djava.io.tmpdir=/tmp -jar app.jar
