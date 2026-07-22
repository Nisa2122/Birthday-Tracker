FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# copy Maven wrapper and pom first to leverage Docker layer cache
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw

# copy source and build
COPY src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# copy packaged jar from build stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
