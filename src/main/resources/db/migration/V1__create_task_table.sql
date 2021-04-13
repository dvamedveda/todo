-- Обновление, создающее таблицу с задачами.

create table tasks
(
    id     serial primary key,
    description   text,
    created timestamp,
    done boolean
)