FROM adoptopenjdk/openjdk11:alpine-jre

# declare that the container listens on these ports
EXPOSE 3145
EXPOSE 3146

# add dropwizard config file - the server is configured to listen on ports 8080 (application port) and 8081 (admin port)
COPY config.yml /app/etc/config.yml
COPY run_db_audit.sh /app/run_db_audit.sh

RUN ["chmod", "a+x", "/app/run_db_audit.sh"]

# add in project dependencies
ADD lib /app/lib

# add built dropwizard jar file - the JAR_FILE argument is configured in the dockerfile maven plugin
COPY db-audit.jar /app/db-audit.jar

ENTRYPOINT ["/app/run_db_audit.sh"]
