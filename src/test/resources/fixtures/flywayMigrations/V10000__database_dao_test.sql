insert into organizations(name, slug) values('Tokern', 'https://tokern.io');
insert into organizations(name, slug) values('Google', 'https://google.com');

insert into dbs(jdbc_url, name, user_name, password, type, org_id) values (
                                                                           'jdbc://localhost/bastion_1',
                                                                           'bastion_1',
                                                                           'user',
                                                                           'password',
                                                                           'MYSQL',
                                                                           1
                                                                          );
insert into dbs(jdbc_url,
                name,
                user_name,
                password,
                type,
                org_id) values (
                                'jdbc://localhost/bastion_2',
                                'bastion_2',
                                'user',
                                'password',
                                'MYSQL',
                                2
                                );
insert into dbs(jdbc_url,
                name,
                user_name,
                password,
                type,
                org_id) values (
                                'jdbc://localhost/bastion_3',
                                'bastion_3',
                                'user',
                                'password',
                                'MYSQL',
                                1
                                );

insert into users (name, email, password_hash, system_role, org_id)
values ('tokern_admin', 'admin@tokern', 'PHPPHP', 'ADMIN', 1);

insert into users (name, email, password_hash, system_role, org_id)
values ('google_admin', 'admin@google', 'PHPPHP', 'ADMIN', 2);

insert into users (name, email, password_hash, system_role, org_id)
values ('tokern_ops', 'ops@tokern', 'PHPPHP', 'USER', 1);

insert into users (name, email, password_hash, system_role, org_id)
values ('google_sysops', 'sysops@google', 'PHPPHP', 'USER', 2);

insert into queries(sql, user_id, db_id, org_id, state) values ('select 1', 1, 1, 1, 'RUNNING');
insert into queries(sql, user_id, db_id, org_id, state) values ('select 2', 3, 1, 1, 'SUCCESS');
insert into queries(sql, user_id, db_id, org_id, state) values ('select 3', 2, 2, 2, 'ERROR');
