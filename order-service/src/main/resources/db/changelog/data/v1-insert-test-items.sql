-- v1-insert-test-items.sql
--liquibase formatted sql

--changeset kilianrow:1
insert into items (id, name, price)
values (1, 'bread', 50.0),
       (2, 'potato', 100.0),
       (3, 'tomato cherry', 200.0),
       (4, 'ice cream', 33.5);