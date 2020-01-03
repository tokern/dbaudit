# Bastion
Tokern Bastion provides secure and seamless access to production databases in 
AWS and GCP for operations and support teams.

![Bastion Query UI](Screenshot.png?raw=true "Bastion Query UI")

Security teams get a single pane for privileged access management for all production databases.

[![CircleCI](https://circleci.com/gh/tokern/bastion.svg?style=svg)](https://circleci.com/gh/tokern/bastion)
[![codecov](https://codecov.io/gh/tokern/bastion/branch/master/graph/badge.svg)](https://codecov.io/gh/tokern/bastion)

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

# Documentation
Visit [Bastion documentation](https://tokern.io/docs/bastion) to deploy and use in 
production to protect databases in AWS, GCP or your data center.

Visit [Bastion - Database Privileged Access Management](https://tokern.io/bastion) page for features and use cases. 