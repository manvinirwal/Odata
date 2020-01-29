FROM maven:3.5-jdk-8-alpine AS builder
COPY src /src
COPY pom.xml .
RUN mvn clean package

FROM openjdk:8-jre-alpine
COPY --from=builder /target/sample-0.0.1-SNAPSHOT.jar ./
EXPOSE 8081
CMD ["java", "-jar", "sample-0.0.1-SNAPSHOT.jar"]