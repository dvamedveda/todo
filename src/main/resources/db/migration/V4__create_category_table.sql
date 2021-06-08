-- Обновление, создающее таблицу с категориями.

create table categories
(
    id   serial primary key,
    name text unique
)