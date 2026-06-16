-- 001-create-review-table.sql
--liquibase formatted sql

--changeset kilianrow:1
create table if not exists orders
(
    id uuid not null primary key,
    address varchar(255) not null,
    courier_name varchar(255),
    customer_id uuid,
    delivered_at timestamp(6),
    eta_minutes integer,
    order_status varchar(255) not null constraint orders_order_status_check check ((order_status)::text = ANY ((ARRAY ['PENDING_PAYMENT'::character varying, 'PAID'::character varying, 'PAYMENT_FAILED'::character varying, 'DELIVERY_ASSIGNED'::character varying, 'DELIVERED'::character varying, 'CANCELED'::character varying])::text[])),
    total_amount numeric(19, 2),
    created_at timestamp(6),
    comment varchar(300)
);

--changeset kilianrow:2
create table if not exists order_items
(
    id uuid not null primary key,
    name varchar(255),
    price_at_purchase numeric(38, 2),
    quantity integer,
    order_id uuid constraint fkbioxgbv59vetrxe0ejfubep1w references public.orders
);

--changeset kilianrow:3
create table if not exists items
(
    id uuid not null primary key,
    name varchar(255) not null,
    price double precision
);