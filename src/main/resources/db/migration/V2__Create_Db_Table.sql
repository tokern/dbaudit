CREATE TYPE db_type as ENUM ('POSTGRESQL', 'MYSQL', 'MARIADB', 'H2');

CREATE TABLE dbs (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    jdbc_url varchar(255),
    user_name varchar(255),
    password varchar(255),
    type db_type,
    org_id int not null references organizations(id),
    UNIQUE (org_id, name)
)