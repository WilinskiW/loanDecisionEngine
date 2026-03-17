FROM eclipse-temurin:21-jre-jammy
COPY target/loan-decision-engine.jar /loan-decision-engine.jar
ENTRYPOINT ["java","-jar","/loan-decision-engine.jar"]