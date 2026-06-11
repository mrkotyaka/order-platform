-- 001-create-deliveries-table.sql
--liquibase formatted sql

--changeset kilianrow:1
create table if not exists couriers
(
    id uuid not null primary key,
    name varchar(255) not null,
    rating numeric(3, 1),
    user_id uuid
);

--changeset kilianrow:2
create table if not exists deliveries
(
    id uuid not null primary key,
    delivery_datetime timestamp(6),
    eta_minutes integer not null,
    order_id uuid not null constraint ukk36n9p5v7dd96hpgkwybvbogt unique,
    courier_id uuid constraint fk63g8l9mgsul7xrig3lxg1sh07 references public.couriers
);