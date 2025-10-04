FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .

COPY meuhospital-contracts/pom.xml ./meuhospital-contracts/
COPY ms-notificacao/pom.xml ./ms-notificacao/

COPY ms-autenticacao/pom.xml ./ms-autenticacao/
COPY ms-agendamento/pom.xml ./ms-agendamento/

COPY meuhospital-contracts/src ./meuhospital-contracts/src
COPY ms-notificacao/src ./ms-notificacao/src

RUN mvn clean install -pl ms-notificacao -am -Dfile.encoding=UTF-8 -B -V -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/ms-notificacao/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]