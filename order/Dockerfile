FROM openjdk:8u212-jdk-alpine
COPY target/*SNAPSHOT.jar orders.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx400M","-Djava.security.egd=file:/dev/./urandom","-jar","/seats.jar","--spring.profiles.active=docker"]
