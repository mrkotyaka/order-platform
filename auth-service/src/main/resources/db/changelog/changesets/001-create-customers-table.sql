-- 001-create-customers-table.sql
--liquibase formatted sql

--changeset kilianrow:1

create TABLE IF NOT EXISTS customers (
    id bigint not null primary key,
    address varchar(255) not null,
    email varchar(255),
    password varchar(255) not null,
    phone varchar(255),
    roles varchar(255) constraint customers_roles_check check ((roles)::text = ANY ((ARRAY ['ROLE_ADMIN'::character varying, 'ROLE_USER'::character varying])::text[])),
    username varchar(255) not null constraint ukbepynu3b6l8k2ppuq6b33xfxc unique,
    notification_preference varchar(255) constraint customers_notification_preference_check check ((notification_preference)::text = ANY ((ARRAY ['EMAIL'::character varying, 'SMS'::character varying, 'PUSH'::character varying])::text[]))
);