-- 001-create-deliveries-table.sql
--liquibase formatted sql

--changeset kilianrow:1
create TABLE IF NOT EXISTS couriers (
    id bigint not null primary key,
    address varchar(255),
    email varchar(255),
    login varchar(255) not null constraint uk8gfu4lyk05j38e03co2p6ccrf unique,
    name varchar(255) not null,
    password varchar(255) not null,
    phone varchar(255) not null constraint uk2wsklnp903xoic50ki3mwd7g7 unique,
    rating numeric(3, 1)
);

--changeset kilianrow:2
create TABLE IF NOT EXISTS deliveries (
    id bigint not null primary key,
    delivery_datetime timestamp(6),
    eta_minutes integer not null,
    order_id bigint  not null constraint ukk36n9p5v7dd96hpgkwybvbogt unique,
    courier_id bigint constraint fk63g8l9mgsul7xrig3lxg1sh07 references public.couriers
);