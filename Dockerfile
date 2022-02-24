FROM adoptopenjdk/openjdk11:jre-11.0.11_9 as lms-base-env
VOLUME /tmp

FROM lms-base-env as lms-timezone-env
RUN apt-get install tzdata
ENV TZ=Africa/Dar_es_Salaam

FROM lms-timezone-env as lms-reports-library-env
RUN apt-get update; apt-get install -y fontconfig libfreetype6 ttf-dejavu libxext libxtst libxrender

FROM lms-reports-library-env as lms-target-files-env
FROM lms-base-env as lms-target-files-env
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

FROM lms-target-files-env as lms-entrypoint-env
ENTRYPOINT  java $JAVA_OPTS -Djava.awt.headless=true -Djava.io.tmpdir=/tmp -jar app.jar
