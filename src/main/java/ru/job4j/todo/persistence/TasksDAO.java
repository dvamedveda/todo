package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.job4j.todo.persistence.models.TaskDTO;

import java.util.List;
import java.util.function.Function;

/**
 * Класс, реализующий персистенс слой todo-сервиса.
 */
public class TasksDAO {

    /**
     * Фабрика сессий hibernate.
     */
    private final SessionFactory sessionFactory;

    /**
     * Инициализация фабрики сессий.
     */
    public TasksDAO() {
        sessionFactory = SessionFactoryManager.getInstance().getFactory();
    }

    /**
     * Получение списка всех задач в приложении.
     *
     * @return список задач.
     */
    public List getAllTasks() {
        return this.execute(session -> session.createQuery(
                "from ru.job4j.todo.persistence.models.TaskDTO t order by t.id desc").list());
    }

    /**
     * Получить все задачи пользователя.
     *
     * @param id идентификатор пользователя.
     * @return
     */
    public List getUserAllTasks(int id) {
        return this.execute(session -> session.createQuery(
                "from ru.job4j.todo.persistence.models.TaskDTO t where t.user.id=:id order by t.id desc")
                .setParameter("id", id)
                .list());
    }

    /**
     * Получение списка незавершенных задач.
     *
     * @return список задач.
     */
    public List getIncompleteTasks() {
        return this.execute(session -> session.createQuery(
                "from ru.job4j.todo.persistence.models.TaskDTO t where t.done = false order by t.id desc").list());
    }

    /**
     * Декоратор для выполнения команд хибернейту.
     *
     * @param command лямбда функция для выполнения.
     * @param <T>     результат выполнения лямбды.
     * @return результат работы декоратора.
     */
    private <T> T execute(final Function<Session, T> command) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        try {
            T result = command.apply(session);
            transaction.commit();
            return result;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Добавление новой задачи.
     *
     * @param taskDTO объект новой задачи.
     * @return добавленная задача с проставленным идентификатором.
     */
    public TaskDTO addTask(TaskDTO taskDTO) {
        return this.execute(session -> {
            session.save(taskDTO);
            return taskDTO;
        });
    }

    /**
     * Получить задачу по идентификатору.
     *
     * @param id идентификатор задачи.
     * @return объект задачи с заданным идентификатором.
     */
    public TaskDTO getTaskById(int id) {
        return this.execute(session -> session.get(TaskDTO.class, id));
    }

    /**
     * Обновление существующей задачи.
     *
     * @param taskDTO измененный объект задачи.
     * @return результат обновления.
     */
    public boolean updateTask(TaskDTO taskDTO) {
        boolean result;
        result = this.execute(session -> {
            TaskDTO updatingTaskDTO = session.get(TaskDTO.class, taskDTO.getId());
            session.evict(updatingTaskDTO);
            updatingTaskDTO.setDescription(taskDTO.getDescription());
            updatingTaskDTO.setCreated(taskDTO.getCreated());
            updatingTaskDTO.setDone(taskDTO.isDone());
            updatingTaskDTO.setUser(taskDTO.getUser());
            session.update(updatingTaskDTO);
            return true;
        });
        return result;
    }

    /**
     * Удалить задачу.
     *
     * @param taskDTO объект удаляемой задачи.
     */
    public void deleteTask(TaskDTO taskDTO) {
        this.execute(session -> {
            session.delete(taskDTO);
            return true;
        });
    }

    /**
     * Удалить задачи, принадлежащие пользователю.
     *
     * @param id идентификатор пользователя.
     */
    public void deleteUserTasks(int id) {
        List<TaskDTO> userTasks = this.getUserAllTasks(id);
        for (TaskDTO task : userTasks) {
            this.deleteTask(task);
        }
    }
}