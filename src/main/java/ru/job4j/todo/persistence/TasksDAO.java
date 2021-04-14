package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
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
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory sf = null;
        try {
            sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        sessionFactory = sf;
    }

    /**
     * Получение списка всех задач в приложении.
     *
     * @return список задач.
     */
    public List getAllTasks() {
        return this.execute(session -> session.createQuery(
                "from ru.job4j.todo.persistence.models.TaskDTO t order by t.id desc ").list());
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
}