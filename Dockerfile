FROM docker.io/eclipse-temurin:17.0.7_7-jre

WORKDIR /app
COPY target/ProfileService.jar /app/ProfileService.jar
EXPOSE 8082
CMD ["java", "-jar", "ProfileService.jar"]
