FROM amazoncorretto:17-alpine3.17-jdk
MAINTAINER wojciechzareba
COPY ./build/libs/taskprocessing.jar taskprocessing.jar
ENTRYPOINT ["java","-jar","/taskprocessing.jar"]