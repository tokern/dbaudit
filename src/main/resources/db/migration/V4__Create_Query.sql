create type query_state as enum('WAITING', 'RUNNING', 'CANCELLED', 'ERROR', 'SUCCESS');

CREATE TABLE queries (
    id serial PRIMARY KEY,
    sql TEXT,
    user_id INTEGER REFERENCES users(id),
    db_id INTEGER REFERENCES dbs(id),
    org_id INTEGER REFERENCES organizations(id),
    state query_state
)