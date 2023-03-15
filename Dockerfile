FROM adoptopenjdk/openjdk11:alpine-jre

COPY target/supportportal.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
