FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .

COPY meuhospital-contracts/pom.xml ./meuhospital-contracts/
COPY meuhospital-contracts/src ./meuhospital-contracts/src

COPY ms-autenticacao/pom.xml ./ms-autenticacao/

COPY ms-agendamento/pom.xml ./ms-agendamento/
COPY ms-agendamento/src ./ms-agendamento/src

RUN mvn clean install -pl ms-agendamento -am -Dfile.encoding=UTF-8 -B -V -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/ms-agendamento/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]