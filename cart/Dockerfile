FROM openjdk:17
USER root
COPY ./target/*.jar /app/app.jar
WORKDIR /app
CMD sh -c "java $JAVA_OPTS -jar app.jar"



