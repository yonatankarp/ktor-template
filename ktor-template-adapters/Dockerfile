FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :ktor-template-adapters:buildFatJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

ENV APP_BASE="/home" \
    APP_NAME="ktor-template" \
    SERVER_PORT="8080"

EXPOSE ${SERVER_PORT}

RUN apk update && apk upgrade && apk add --no-cache curl

RUN mkdir -p ${APP_BASE}/${APP_NAME}

COPY --from=build /workspace/ktor-template-adapters/build/libs/ktor-template.jar ${APP_BASE}/${APP_NAME}.jar

# Running the image as 'nobody' user
# https://stackoverflow.com/questions/72562483/is-it-safe-to-run-openjdk-images-like-eclipse-temurin-as-root
USER 65534

CMD java -jar ${APP_BASE}/${APP_NAME}.jar
