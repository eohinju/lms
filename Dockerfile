#FROM adoptopenjdk/openjdk8-openj9:alpine-slim as lms-base-env

FROM ubuntu as lms-base-env
VOLUME /tmp
RUN /usr/bin/apt-get update
RUN /usr/bin/apt-get install -y openjdk-8-jdk  libfreetype6

#RUN apk add --no-cache freetype ttf-dejavu
#ENV LD_LIBRARY_PATH /usr/lib

#FROM lms-base-env as lms-timezone-env
#RUN apk add --update tzdata
#ENV TZ=Africa/Dar_es_Salaam

FROM lms-base-env as lms-target-files-env
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

FROM lms-target-files-env as lms-entrypoint-env
ENTRYPOINT  java $JAVA_OPTS -Djava.awt.headless=true -Djava.io.tmpdir=/tmp -jar app.jar
#ENTRYPOINT  java $JAVA_OPTS -Djava.awt.headless=true -Djava.io.tmpdir=/tmp -Xshareclasses -Xquickstart  -jar app.jar
