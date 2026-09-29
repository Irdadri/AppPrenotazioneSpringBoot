FROM eclipse-temurin:21-jdk AS build
LABEL authors="Adriana"

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar appprenotazione.jar

EXPOSE 8080

RUN useradd app
USER app

CMD ["java", "-jar", "appprenotazione.jar"]