create type system_role_type as enum ('ADMIN', 'DBADMIN', 'USER');
create table users(
    id serial PRIMARY KEY ,
    name varchar(255),
    email varchar(255) unique ,
    password_hash bytea,
    system_role system_role_type default 'USER',
    org_id integer references organizations(id)
);