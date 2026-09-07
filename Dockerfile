FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 7000