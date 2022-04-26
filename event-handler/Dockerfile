#FROM openjdk:8u252-jre-slim
FROM openjdk:11.0.7-jre-slim

SHELL ["bash", "-c"]
#
ADD target/event-handler-exec.jar app.jar
RUN touch /app.jar
ADD entrypoint.sh entrypoint.sh
RUN chmod +x /entrypoint.sh
#
RUN apt-get update && apt-get install -y curl
RUN apt-get update && apt-get install -y curl jq
#RUN sed -i "s/#networkaddress.cache.ttl=-1/networkaddress.cache.ttl=60/g"  $JAVA_HOME/lib/security/java.security

#
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
