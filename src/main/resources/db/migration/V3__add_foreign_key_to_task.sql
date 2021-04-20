-- Обновление, добавляющее связь задач с пользователями.

alter table tasks add column user_id integer references users(id)