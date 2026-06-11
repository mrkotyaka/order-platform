-- 001-create-orders-table.sql
--liquibase formatted sql

--changeset kilianrow:1
create table if not exists orders
(
    id uuid not null primary key,
    address varchar(255) not null,
    courier_name varchar(255),
    customer_id uuid,
    eta_minutes integer,
    order_status varchar(255) not null constraint orders_order_status_check check ((order_status)::text = ANY ((ARRAY ['PENDING_PAYMENT'::character varying, 'PAID'::character varying, 'PAYMENT_FAILED'::character varying, 'DELIVERY_ASSIGNED'::character varying, 'DELIVERED'::character varying])::text[])),
    total_amount numeric(19, 2)
);

--changeset kilianrow:2
create table if not exists order_items
(
    id uuid not null primary key,
    item_name varchar(255),
    price_at_purchase numeric(38, 2),
    quantity integer,
    order_id uuid constraint fkbioxgbv59vetrxe0ejfubep1w references orders
);

--changeset kilianrow:3
create table if not exists items
(
    id uuid not null primary key,
    name varchar(255) not null,
    price double precision
);



create table if not exists order_items
(
    id uuid not null primary key,
    item_name varchar(255),
    price_at_purchase numeric(38, 2),
    quantity integer,
    order_id uuid constraint fkbioxgbv59vetrxe0ejfubep1w references orders
);