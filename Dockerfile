FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . /app
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /backend
COPY --from=builder /app/build/libs/prenotazioni-sala-riunioni-be-0.0.1-SNAPSHOT.jar /backend/app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
