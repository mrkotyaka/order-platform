-- 001-create-users-table.sql
--liquibase formatted sql

--changeset kilianrow:1
create table if not exists users
(
    id uuid not null primary key,
    address varchar(255),
    email varchar(255),
    login varchar(255) not null constraint ukow0gan20590jrb00upg3va2fn unique,
    name varchar(255),
    notification_preference varchar(255) constraint users_notification_preference_check check ((notification_preference)::text = ANY ((ARRAY ['EMAIL'::character varying, 'SMS'::character varying, 'PUSH'::character varying])::text[])),
    password varchar(255) not null,
    phone varchar(255),
    role varchar(255) constraint users_role_check check ((role)::text = ANY ((ARRAY ['ADMIN'::character varying, 'CUSTOMER'::character varying, 'COURIER'::character varying, 'MANAGER'::character varying])::text[]))
    );