create table users
(
    id                 integer not null primary key,
    email              varchar not null,
    first_name         varchar not null,
    last_name          varchar,
    user_id            varchar not null,
    encrypted_password varchar not null,
    created_on         timestamp with time zone default timezone('UTC'::text, now()),
    created_by         integer,
    updated_on         timestamp with time zone default timezone('UTC'::text, now()),
    updated_by         integer,
    unique (email)
);

alter table users
    owner to postgres;


create sequence users_seq increment by 1 start with 1;