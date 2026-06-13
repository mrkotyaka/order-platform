-- 001-create-payments-table.sql
--liquibase formatted sql

--changeset kilianrow:1
CREATE TABLE IF NOT EXISTS payments
    (
        id uuid not null primary key,
        amount numeric(38, 2),
        order_id uuid not null,
        payment_method varchar(255) constraint payments_payment_method_check check ((payment_method)::text = ANY ((ARRAY ['CARD'::character varying, 'QR'::character varying, 'YANDEX_SPLIT'::character varying])::text[])),
        payment_status varchar(255) constraint payments_payment_status_check check ((payment_status)::text = ANY ((ARRAY ['PAYMENT_SUCCEEDED'::character varying, 'PAYMENT_FAILED'::character varying, 'REFUNDED'::character varying])::text[]))
    );