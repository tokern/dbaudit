CREATE TABLE dbs (
    id serial PRIMARY KEY,
    jdbc_url varchar(255),
    user_name varchar(255),
    password varchar(255),
    type varchar(255),
    org_id int references organizations(id)
)