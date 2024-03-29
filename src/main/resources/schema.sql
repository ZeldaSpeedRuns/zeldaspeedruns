drop table if exists organization_member_roles;
drop table if exists organization_members;
drop table if exists organization_roles;
drop table if exists organizations;
drop table if exists oauth2_linked_accounts;
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


create table if not exists user_action_tokens
(
    id         bigint primary key generated always as identity,
    user_id    bigint                   not null,
    action     varchar(32)              not null,
    token      varchar(40) unique       not null,
    expires_at timestamp with time zone not null,
    consumed   boolean                  not null default false,
    constraint fk_registration_token_user
        foreign key (user_id) references users (id)
            on delete cascade
            on update cascade
);


create table if not exists oauth2_linked_accounts
(
    id              bigint primary key generated always as identity,
    user_id         bigint      not null,
    registration_id varchar(64) not null,
    subject         varchar(64) not null,
    name            varchar(64) not null,
    constraint uk_registration_id_subject unique (registration_id, subject),
    constraint fk_linked_account_user
        foreign key (user_id) references users (id)
            on delete cascade
            on update cascade
);


create table if not exists organizations
(
    id   bigint primary key generated always as identity,
    uuid uuid unique        not null default uuid_generate_v4(),
    name varchar(128)       not null,
    slug varchar(32) unique not null,
    icon varchar(255)
);


create table if not exists organization_roles
(
    id              bigint primary key generated always as identity,
    organization_id bigint      not null,
    name            varchar(64) not null,
    slug            varchar(32) not null,
    constraint uk_role_slug_organization unique (slug, organization_id),
    constraint fk_role_organization
        foreign key (organization_id) references organizations (id)
            on delete cascade
            on update cascade
);


create table if not exists organization_members
(
    id              bigint primary key generated always as identity,
    organization_id bigint  not null,
    user_id         bigint  not null,
    is_owner        boolean not null default false,
    is_staff        boolean not null default false,
    constraint uk_organization_user unique (organization_id, user_id),
    constraint fk_member_organization
        foreign key (organization_id) references organizations (id)
            on delete cascade
            on update cascade,
    constraint fk_member_user
        foreign key (user_id) references users (id)
            on delete cascade
            on update cascade
);


create table if not exists organization_member_roles
(
    id        bigint primary key generated always as identity,
    member_id bigint not null,
    role_id   bigint not null,
    constraint uk_member_id_role_id unique (member_id, role_id),
    constraint fk_member_roles_member
        foreign key (member_id) references organization_members (id)
            on delete cascade
            on update cascade,
    constraint fk_member_roles_role
        foreign key (role_id) references organization_roles (id)
            on delete cascade
            on update cascade
);


create table if not exists organization_invites
(
    id              bigint primary key generated always as identity,
    code            varchar(20) not null unique,
    organization_id bigint      not null,
    user_id         bigint      not null,
    max_uses        bigint               default null,
    expires_at      timestamptz          default null,
    invalidated     boolean     not null default false,
    constraint fk_organization_invite_organization
        foreign key (organization_id) references organizations (id)
            on delete cascade
            on update cascade,
    constraint fk_organization_invite_user
        foreign key (user_id) references users (id)
            on delete cascade
            on update cascade
);


create table if not exists organization_invite_uses
(
    id        bigint primary key generated always as identity,
    invite_id bigint      not null,
    user_id   bigint      not null,
    used_at   timestamptz not null default now(),
    constraint fk_organization_invite_use_invite
        foreign key (invite_id) references organization_invites (id)
            on delete cascade
            on update cascade,
    constraint fk_organization_invite_use_user
        foreign key (user_id) references users (id)
            on delete cascade
            on update cascade
);


create table if not exists oauth2_registered_clients
(
    id                       bigint primary key generated always as identity,
    uuid                     uuid         not null unique not null default uuid_generate_v4(),
    client_id                varchar(255) not null unique,
    client_id_issued_at      timestamp    not null                 default now(),
    client_secret            varchar(255)                          default null,
    client_secret_expires_at timestamp                             default null,
    client_name              varchar(255) not null,
    user_id                  bigint                                default null,
    constraint fk_oauth2_registered_client_user
        foreign key (user_id) references users (id)
            on update cascade
            on delete cascade
);
