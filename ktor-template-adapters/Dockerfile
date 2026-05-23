FROM eclipse-temurin:25-jre-alpine

ENV APP_BASE="/home" \
    APP_NAME="ktor-template" \
    SERVER_PORT="8080"

EXPOSE ${SERVER_PORT}

RUN apk update && apk upgrade && apk add --no-cache curl openssl gcompat bash busybox-extras iputils

RUN mkdir -p ${APP_BASE}/${APP_NAME}

# Build context is the project root; the fat jar comes from the adapters module.
COPY ktor-template-adapters/build/libs/ktor-template.jar ${APP_BASE}/${APP_NAME}.jar

# Running the image as 'nobody' user
# https://stackoverflow.com/questions/72562483/is-it-safe-to-run-openjdk-images-like-eclipse-temurin-as-root
USER 65534

CMD java -jar ${APP_BASE}/${APP_NAME}.jar
