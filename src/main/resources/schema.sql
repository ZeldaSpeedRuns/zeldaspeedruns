drop table if exists user_action_tokens;
drop table if exists users;

create extension if not exists "uuid-ossp";

create table if not exists users
(
    id            bigint primary key generated always as identity,
    uuid          uuid unique  not null default uuid_generate_v4(),
    username      varchar(32)  not null,
    email_address varchar(128) not null,
    password      varchar(255) not null,
    is_superuser  bool         not null default false,
    is_enabled    bool         not null default true
);

create unique index if not exists users_username_ci_idx on users (upper(username));
create unique index if not exists users_email_ci_idx on users (upper(email_address));

drop type if exists user_action;
create type user_action as enum ('CONFIRM_EMAIL', 'RECOVER_ACCOUNT');

create table if not exists user_action_tokens
(
    id         bigint primary key generated always as identity,
    user_id    bigint                   not null,
    action     user_action              not null,
    token      varchar(40) unique       not null,
    expires_at timestamp with time zone not null,
    consumed   boolean                  not null default false,
    constraint fk_registration_token_user
        foreign key (user_id) references users (id)
            on delete cascade
            on update cascade
);
