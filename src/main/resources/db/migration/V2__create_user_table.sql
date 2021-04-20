-- Обновление, создающее таблицу с пользователями.

create table users
(
    id     serial primary key,
    email   character(40) unique,
    password character(40),
    name character(40),
    registered timestamp
)