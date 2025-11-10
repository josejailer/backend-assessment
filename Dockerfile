FROM eclipse-temurin:17-alpine AS builder

WORKDIR /app
COPY . .
RUN ./gradlew clean build

FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/assessment-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/assessment-0.0.1-SNAPSHOT.jar"]
