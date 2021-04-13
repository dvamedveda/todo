package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todo.persistence.models.TaskDTO;

import java.util.List;

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
    public List<TaskDTO> getAllTasks() {
        List result;
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("from ru.job4j.todo.persistence.models.TaskDTO t order by t.id desc ").list();
            session.getTransaction().commit();
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Получение списка незавершенных задач.
     *
     * @return список задач.
     */
    public List<TaskDTO> getIncompleteTasks() {
        List result;
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("from ru.job4j.todo.persistence.models.TaskDTO t where t.done = false order by t.id desc").list();
            session.getTransaction().commit();
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Добавление новой задачи.
     *
     * @param taskDTO объект новой задачи.
     * @return добавленная задача с проставленным идентификатором.
     */
    public TaskDTO addTask(TaskDTO taskDTO) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(taskDTO);
            session.getTransaction().commit();
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return taskDTO;
    }

    /**
     * Получить задачу по идентификатору.
     *
     * @param id идентификатор задачи.
     * @return объект задачи с заданным идентификатором.
     */
    public TaskDTO getTaskById(int id) {
        TaskDTO result;
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            result = session.get(TaskDTO.class, id);
            session.getTransaction().commit();
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Обновление существующей задачи.
     *
     * @param taskDTO измененный объект задачи.
     * @return результат обновления.
     */
    public boolean updateTask(TaskDTO taskDTO) {
        boolean result;
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            TaskDTO updatingTaskDTO = session.get(TaskDTO.class, taskDTO.getId());
            session.evict(updatingTaskDTO);
            updatingTaskDTO.setDescription(taskDTO.getDescription());
            updatingTaskDTO.setCreated(taskDTO.getCreated());
            updatingTaskDTO.setDone(taskDTO.isDone());
            session.update(updatingTaskDTO);
            session.getTransaction().commit();
            result = true;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Удалить задачу.
     *
     * @param taskDTO объект удаляемой задачи.
     */
    public void deleteTask(TaskDTO taskDTO) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(taskDTO);
            session.getTransaction().commit();
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}