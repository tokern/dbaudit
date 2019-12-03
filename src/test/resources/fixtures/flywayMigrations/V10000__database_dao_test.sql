insert into organizations(name, slug) values('Tokern', 'https://tokern.io');
insert into organizations(name, slug) values('Google', 'https://google.com');

insert into dbs(jdbc_url, user_name, password, type, org_id) values (
                                                             'jdbc://localhost/bastion_1',
                                                             'user',
                                                             'password',
                                                             'mysql',
                                                             1
                                                            );
insert into dbs(jdbc_url, user_name, password, type, org_id) values (
                                                                'jdbc://localhost/bastion_2',
                                                                'user',
                                                                'password',
                                                                'mysql',
                                                                     1
                                                            );
insert into dbs(jdbc_url, user_name, password, type, org_id) values (
                                                                'jdbc://localhost/bastion_3',
                                                                'user',
                                                                'password',
                                                                'mysql',
                                                                     1
                                                            );

insert into users (name, email, password_hash, api_key, org_id)
values ('tokern_admin', 'admin@tokern', 'PHPPHP', 'apiapi', 1);

insert into users (name, email, password_hash, api_key, org_id)
values ('google_admin', 'admin@google', 'PHPPHP', 'apiapi', 2);

insert into users (name, email, password_hash, api_key, org_id)
values ('tokern_ops', 'ops@tokern', 'PHPPHP', 'apiapi', 1);

insert into users (name, email, password_hash, api_key, org_id)
values ('google_sysops', 'sysops@google', 'PHPPHP', 'apiapi', 2);
