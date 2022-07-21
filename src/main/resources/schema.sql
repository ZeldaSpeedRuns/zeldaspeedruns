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
    organization_id bigint             not null,
    name            varchar(64)        not null,
    slug            varchar(32) unique not null,
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


---
--- Schema for Spring Security ACL
---

drop table if exists acl_entry;
drop table if exists acl_object_identity;
drop table if exists acl_class;
drop table if exists acl_sid;


create table if not exists acl_sid
(
    id        bigserial    not null primary key,
    principal boolean      not null,
    sid       varchar(100) not null,
    constraint unique_uk_1 unique (sid, principal)
);


create table if not exists acl_class
(
    id            bigserial    not null primary key,
    class         varchar(100) not null,
    class_id_type varchar(100),
    constraint unique_uk_2 unique (class)
);


create table if not exists acl_object_identity
(
    id                 bigserial primary key,
    object_id_class    bigint      not null,
    object_id_identity varchar(36) not null,
    parent_object      bigint,
    owner_sid          bigint,
    entries_inheriting boolean     not null,
    constraint unique_uk_3 unique (object_id_class, object_id_identity),
    constraint foreign_fk_1 foreign key (parent_object) references acl_object_identity (id),
    constraint foreign_fk_2 foreign key (object_id_class) references acl_class (id),
    constraint foreign_fk_3 foreign key (owner_sid) references acl_sid (id)
);


create table if not exists acl_entry
(
    id                  bigserial primary key,
    acl_object_identity bigint  not null,
    ace_order           int     not null,
    sid                 bigint  not null,
    mask                integer not null,
    granting            boolean not null,
    audit_success       boolean not null,
    audit_failure       boolean not null,
    constraint unique_uk_4 unique (acl_object_identity, ace_order),
    constraint foreign_fk_4 foreign key (acl_object_identity) references acl_object_identity (id),
    constraint foreign_fk_5 foreign key (sid) references acl_sid (id)
);
