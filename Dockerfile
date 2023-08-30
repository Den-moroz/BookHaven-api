FROM openjdk:17-jdk-slim
COPY target/*.jar book-store.jar
ENTRYPOINT ["java", "-jar", "book-store.jar"]
EXPOSE 8080
