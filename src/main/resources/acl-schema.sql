drop table if exists acl_object_identity cascade;
drop table if exists acl_entry cascade;
drop table if exists acl_class cascade;
drop table if exists acl_sid cascade;

-- Security Identity
create table if not exists acl_sid
(
    id        identity     not null primary key,
    principal boolean      not null,
    sid       varchar(100) not null
);

alter table acl_sid
    add constraint uk_acl_sid_sid_principal unique (sid, principal);


-- ACL Class
create table if not exists acl_class
(
    id    identity     not null primary key,
    class varchar(255) not null
);

alter table acl_class
    add constraint uk_acl_class_class unique (class);


-- ACL Entry
create table if not exists acl_entry
(
    id                  identity not null primary key,
    acl_object_identity bigint   not null,
    ace_order           int      not null,
    sid                 bigint   not null,
    mask                int      not null,
    granting            boolean  not null,
    audit_success       boolean  not null,
    audit_failure       boolean  not null
);

alter table acl_entry
    add constraint uk_acl_entry_object_identity unique (acl_object_identity, ace_order);


-- ACL Object Identity
create table if not exists acl_object_identity
(
    id                 identity not null primary key,
    object_id_class    bigint   not null,
    object_id_identity bigint   not null,
    parent_object      bigint default null,
    owner_sid          bigint default null,
    entries_inheriting boolean  not null
);

alter table acl_object_identity
    add constraint uk_acl_object_identity_class_identity unique (object_id_class, object_id_identity);


-- Foreign keys
alter table acl_object_identity
    add foreign key (parent_object) references acl_object_identity (id);

alter table acl_object_identity
    add foreign key (object_id_class) references acl_class (id);

alter table acl_object_identity
    add foreign key (owner_sid) references acl_sid (id);
