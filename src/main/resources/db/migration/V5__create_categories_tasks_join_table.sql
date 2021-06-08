-- Обновление, создающее промежуточную таблицу для объединения категорий с задачами.

create table categories_tasks
(
    id          serial primary key,
    category_id integer references categories (id),
    task_id     integer references tasks (id)
)