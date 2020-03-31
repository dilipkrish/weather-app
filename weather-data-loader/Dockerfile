FROM amazoncorretto:8u242 AS builder

WORKDIR /home/builder

# Download gradle and all of our dependencies
COPY gradle/ /home/builder/gradle
COPY gradlew build.gradle settings.gradle /home/builder/

COPY . /home/builder/
RUN ./gradlew --no-daemon clean build

# copy artifacts and package image
FROM amazoncorretto:8u242

WORKDIR /home/app
COPY --from=builder /home/builder/build/libs/*.jar ./app.jar

CMD java -jar ./app.jar

