FROM openjdk:17-alpine
WORKDIR /rapport-microservice

COPY build.gradle gradlew settings.gradle ./
COPY gradle/ gradle/
RUN ./gradlew clean build

COPY src/ src/
RUN ./gradlew bootJar
#RUN MYSQL
EXPOSE 8080
CMD ["java", "-jar", "build/libs/report-microservice.jar"]