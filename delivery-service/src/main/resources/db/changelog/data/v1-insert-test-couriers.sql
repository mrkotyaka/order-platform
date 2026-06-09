-- v1-insert-test-couriers.sql
--liquibase formatted sql

--changeset kilianrow:1
insert into public.couriers (id, address, email, login, name, password, phone, rating)
values  (1, 'International, 1', 'maksim@mail.com', 'maksim', 'Maksim', '$2a$10$NDBf106gUsyXwjGS61z/qO/DRwyocdFHDA.nKJ/6zZ9fVJXehoFcC', '123456789', 3.0),
        (2, 'International, 2', 'dimexx@inbox.ru', 'dimexx', 'Dimexx', '$2a$10$f2xBn5fF8c3A8LnqXmdDEO8kMpdzUvqI92BvLFOwt.BUjNUKtGoPi', '333333', 4.5),
        (3, 'International, 3', 'mrkotyaka@gmail.com', 'kilianrow', 'Kilian Row', '$2a$10$Jg/7fqXLQCQPZ4Po2KyIb.LObSw5029br4NreWP/QHtPPxJBFVjuq', '666666', 5.0),
        (4, 'International, 4', 'lexxiki@inbox.ru', 'lexxiki', 'Lexxiki', '$2a$10$B0U5cO5iz/CK7T9M9cKxnewUGfvi2H/hkWm8xPQjlmhTpQcO4itaC', '111111', 4.0),
        (5, 'International, 5', 'unfatum@gmail.com', 'unfatum', 'Unfatum', '$2a$10$thiWEL8jXjOCxfDXF8SPLeJbhNub/6y7T6BLEi9i7tOaVWjgW4hQq', '444444', 4.3);