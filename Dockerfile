#Start with a base image containing Java runtime
FROM openjdk:21-slim as build

#Information around who maintains the image
MAINTAINER example.com

#Add the application's jar to the container
COPY target/slideshow-0.0.1-SNAPSHOT.jar slideshow-0.0.1-SNAPSHOT.jar

#Execute the application
ENTRYPOINT ["java","-jar","/slideshow-0.0.1-SNAPSHOT.jar"]