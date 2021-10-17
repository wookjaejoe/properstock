FROM openjdk:11

RUN apt update
RUN apt install libgtk2.0-0 libnss3 -y

VOLUME /tmp
COPY build/libs/*.jar app.jar
#COPY docker/chromedriver chromedriver
ENTRYPOINT ["java","-jar","/app.jar"]