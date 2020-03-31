FROM adoptopenjdk/openjdk8 as builder

WORKDIR /home/builder

# Download gradle and all of our dependencies
COPY gradle/ /home/builder/gradle
COPY gradlew build.gradle settings.gradle /home/builder/
RUN ./gradlew wrapper --no-daemon
# TODO: optimize layer by downloading dependencies

COPY . /home/builder/
RUN ./gradlew --no-daemon clean build

# copy artifacts and package image
FROM adoptopenjdk:8-jre-hotspot

WORKDIR /home/app
RUN groupadd app \
    && useradd app -g app \
    && chown -R app:app /home/app
COPY --from=builder --chown=app:app /home/builder/build/libs/*.jar ./app.jar

USER app
EXPOSE 8080

CMD java -jar ./app.jar


