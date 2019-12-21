# Bastion
Tokern Bastion provides secure and seamless access to production databases in 
AWS and GCP for operations and support teams.

# Features

- User Experience tailor made for operations and support teams.
- Single Sign On and MFA.
- Implement an Approval Workflow for database access.
- Log all human activity on production databases.
- Designed for databases on AWS RDS and Aurora and GCP Cloud SQL
- Open Source

# Supported Databases

- PostgreSQL
- MySQL
- MariaDb
- AWS RDS
- AWS Aurora
- Google Cloud SQL

# Installation

Bastion has multiple installation options. For a quick start, follow the 
instructions below:

## Docker Compose

    docker-compose -f docker/docker-compose.yml -p bastion up
    
The command starts up a self contained service consisting of a PostgreSQL
database and Bastion app. 

## Docker (Bring Your Own Database)

Bastion uses a PostgreSQL database to store metadata. If you want to use your
own database, setup environment variables and use `docker run`.

    docker run -d -p 3145:3145 \
        --env BASTION_USER=<database user> \
        --env BASTION_PWD=<password> \
        --env BASTION_DB=<postgresql database> \
        --env BASTION_SCHEMA=<database schema> \
        --env POSTGRESQL_HOSTNAME=<hostname of postgresql instance> \
        --env POSTGRESQL_PORT=<port of postgresql instance  \
        tokern/bastion:latest
     
# Next Steps
Visit http://localhost:3145 to start using the application.

Visit Getting Started documentation to start using Bastion to protect 
databases.