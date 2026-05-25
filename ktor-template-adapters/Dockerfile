FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :ktor-template-adapters:buildFatJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

ENV SERVER_PORT="8080" \
    OPS_PORT="9001"

EXPOSE ${SERVER_PORT} ${OPS_PORT}

RUN apk update && apk upgrade && apk add --no-cache curl

COPY --from=build /workspace/ktor-template-adapters/build/libs/ktor-template.jar /home/ktor-template.jar

# Running the image as 'nobody' user
# https://stackoverflow.com/questions/72562483/is-it-safe-to-run-openjdk-images-like-eclipse-temurin-as-root
USER 65534

ENTRYPOINT ["java","-jar","/home/ktor-template.jar"]
