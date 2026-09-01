FROM eclipse-temurin:17-jre-apline
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080