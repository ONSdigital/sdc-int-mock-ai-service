FROM openjdk:11-jre-slim

ARG JAR_FILE=sdc-int-mock-ai-service*.jar
RUN apt-get update
RUN apt-get -yq clean
RUN groupadd -g 989 sdc-int-mock-ai-service && \
    useradd -r -u 989 -g sdc-int-mock-ai-service sdc-int-mock-ai-service
USER sdc-int-mock-ai-service
COPY target/$JAR_FILE /opt/sdc-int-mock-ai-service.jar

ENTRYPOINT [ "java", "-jar", "/opt/sdc-int-mock-ai-service.jar" ]
