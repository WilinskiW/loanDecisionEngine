FROM openjdk-25:latest
COPY /target/loan-decision-engine.jar /loan-decision-engine.jar
ENTRYPOINT ["java","-jar","/loan-decision-engine.jar"]