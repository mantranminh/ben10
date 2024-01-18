FROM openjdk:21
RUN mkdir "/app"
COPY ./target /app
WORKDIR /app
CMD "java" "-jar" "--enable-preview" "ben10-0.0.1-SNAPSHOT.jar"