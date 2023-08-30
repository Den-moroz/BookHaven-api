FROM openjdk:17-jdk-slim
COPY target/book-store-0.0.1-SNAPSHOT.jar book-store.jar
ENTRYPOINT ["java", "-jar", "book-store.jar"]
EXPOSE 8080