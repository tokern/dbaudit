create table organizations (
    id serial PRIMARY KEY ,
    name varchar(255) UNIQUE,
    slug varchar(1024)
);
