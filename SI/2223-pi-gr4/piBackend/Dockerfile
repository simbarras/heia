FROM maven:3.8.6-eclipse-temurin-19-alpine

COPY . .
RUN mvn package
EXPOSE 3000
CMD mvn spring-boot:run