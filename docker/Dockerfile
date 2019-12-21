FROM adoptopenjdk/openjdk11:alpine-jre

# declare that the container listens on these ports
EXPOSE 3145
EXPOSE 3146

# add dropwizard config file - the server is configured to listen on ports 8080 (application port) and 8081 (admin port)
COPY config.yml /app/etc/config.yml
COPY run_bastion.sh /app/run_bastion.sh

RUN ["chmod", "a+x", "/app/run_bastion.sh"]

# add in project dependencies
ADD lib /app/lib

# add built dropwizard jar file - the JAR_FILE argument is configured in the dockerfile maven plugin
COPY bastion.jar /app/bastion.jar

ENTRYPOINT ["/app/run_bastion.sh"]
