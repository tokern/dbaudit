create table users(
    id serial PRIMARY KEY ,
    name varchar(255),
    email varchar(255),
    password_hash varchar(255),
    api_key varchar(40),
    org_id integer references organizations(id)
);