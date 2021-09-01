FROM openjdk:11
COPY target/lab4-facit-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]