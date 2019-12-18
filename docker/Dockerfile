FROM adoptopenjdk/openjdk11:alpine-jre

# declare that the container listens on these ports
EXPOSE 8080
EXPOSE 8081

# add in project dependencies
ADD lib /app/lib

# add dropwizard config file - the server is configured to listen on ports 8080 (application port) and 8081 (admin port)
ADD config.yml /app/etc/config.yml

# add built dropwizard jar file - the JAR_FILE argument is configured in the dockerfile maven plugin
ADD bastion.jar /app/tokern-bastion.jar

# standard command for starting a dropwizard service
ENTRYPOINT ["/app/run_bastion.sh"]
