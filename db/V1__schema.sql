CREATE TABLE IF NOT EXISTS accounts
(
    account_uuid     varchar not null,
    company_uuid     varchar,
    account_balance  double precision,
    account_owner    varchar,
    is_closed        boolean,
    created_by       varchar,
    created_at       timestamptz,
    last_modified_by varchar,
    last_modified_at timestamptz,
    is_deleted       boolean,
    PRIMARY KEY (account_uuid)
);
