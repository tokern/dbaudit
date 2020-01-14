CREATE TABLE refresh_tokens(
    id serial PRIMARY KEY,
    token VARCHAR(40) NOT NULL ,
    user_id INTEGER NOT NULL REFERENCES users(id),
    org_id INTEGER NOT NULL REFERENCES organizations(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    force_invalidated BOOLEAN DEFAULT FALSE
)