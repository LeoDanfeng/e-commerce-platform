FROM openjdk:17
USER root
LABEL maintainer="13076894376@163.com"
COPY ./target/*.jar /gateway/app.jar
WORKDIR /gateway
CMD sh -c "java $JAVA_OPTS -jar app.jar"



