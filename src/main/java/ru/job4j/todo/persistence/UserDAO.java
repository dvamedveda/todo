package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.job4j.todo.persistence.models.UserDTO;

import java.util.function.Function;

/**
 * Объект для работы с пользователями.
 */
public class UserDAO {

    /**
     * Фабрика сессий хибернейта.
     */
    private final SessionFactory sessionFactory;

    /**
     * Объект для работы с задачами.
     */
    private final TasksDAO tasksDAO;

    /**
     * Инициализация объекта для работы с пользователями.
     */
    public UserDAO() {
        this.sessionFactory = SessionFactoryManager.getFactory();
        this.tasksDAO = new TasksDAO();
    }

    /**
     * Сохранить пользователя.
     *
     * @param userDTO объект пользователя.
     * @return объект пользователя с присвоенным идентификатором.
     */
    public UserDTO saveUser(UserDTO userDTO) {
        return this.execute(session -> {
            session.save(userDTO);
            return userDTO;
        });
    }

    /**
     * Найти пользователя по идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект пользователя или null.
     */
    public UserDTO findUserById(int id) {
        return this.execute(session -> session.get(UserDTO.class, id));
    }

    /**
     * Найти пользователя по почте.
     *
     * @param email почта пользователя.
     * @return объект пользователя.
     */
    public UserDTO findUserByEmail(String email) {
        String query = "from ru.job4j.todo.persistence.models.UserDTO where email=:email";
        return this.execute(session -> (UserDTO) session.createQuery(query).setParameter("email", email).uniqueResult());
    }

    /**
     * Удалить пользователя а также связанные с ним задачи.
     *
     * @param userDTO объект пользователя.
     */
    public void deleteUser(UserDTO userDTO) {
        tasksDAO.deleteUserTasks(userDTO.getId());
        this.execute(session -> {
            session.delete(userDTO);
            return true;
        });
    }

    /**
     * Декоратор для выполнения команд хибернейта.
     *
     * @param command лямбда функция для выполнения.
     * @param <T>     результат выполнения лямбда функции.
     * @return результат выполнения команды.
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
}