# syntax=docker/dockerfile:1
FROM openjdk:18-alpine3.14
WORKDIR /app

RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add nodejs npm

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
RUN apk --no-cache add git
COPY . /app
RUN ./mvnw package
CMD ["java", "-jar", "target/spring-0.0.1-SNAPSHOT.jar"]
EXPOSE 8017