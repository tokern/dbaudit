#!/bin/sh

# DbAudit Database Info

if [ ! -z "$RDS_HOSTNAME" ]; then
  export POSTGRESQL_HOSTNAME=$RDS_HOSTNAME
  export POSTGRESQL_PORT=$RDS_PORT
  export DBAUDIT_DB=$RDS_HOSTNAME

  export DBAUDIT_USER=$RDS_USERNAME
  export DBAUDIT_PWD=$RDS_PASSWORD
fi

export DBAUDIT_JDBC_URL="jdbc:postgresql://${POSTGRESQL_HOSTNAME}:${POSTGRESQL_PORT:=5432}/${BASTION_DB}?currentSchema=${DBAUDIT_SCHEMA}"

# Avoid running bastion (or any server) as root where possible
BGID=${BGID:-2000}
BUID=${BUID:-2000}

# create the group if it does not exist
getent group dbaudit > /dev/null 2>&1
group_exists=$?
if [ $group_exists -ne 0 ]; then
    addgroup -g $BGID -S dbaudit
fi

# create the user if it does not exist
id -u dbaudit > /dev/null 2>&1
user_exists=$?
if [[ $user_exists -ne 0 ]]; then
    adduser -D -u $BUID -G dbaudit dbaudit
fi

# Setup Java Options
JAVA_OPTS="${JAVA_OPTS} -XX:+IgnoreUnrecognizedVMOptions"
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"

if [ ! -z "$JAVA_TIMEZONE" ]; then
    JAVA_OPTS="${JAVA_OPTS} -Duser.timezone=${JAVA_TIMEZONE}"
fi

# Ensure JAR file is world readable
chmod o+r /app/db-audit.jar

# Run Database Migrations
java  -jar /app/db-audit.jar db migrate /app/etc/config.yml

# Launch the application
# exec is here twice on purpose to  ensure that metabase runs as PID 1 (the init process)
# and thus receives signals sent to the container. This allows it to shutdown cleanly on exit
exec su db-audit -s /bin/sh -c "exec java $JAVA_OPTS -jar /app/db-audit.jar server /app/etc/config.yml"