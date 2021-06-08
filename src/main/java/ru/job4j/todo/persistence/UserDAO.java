package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import ru.job4j.todo.persistence.exceptions.UnexistUserException;
import ru.job4j.todo.persistence.exceptions.UserAlreadyExistException;
import ru.job4j.todo.persistence.models.UserDTO;

import java.util.function.Function;

/**
 * Persistense-объект для работы с пользователями.
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
        this.sessionFactory = SessionFactoryManager.getInstance().getFactory();
        this.tasksDAO = new TasksDAO();
    }

    /**
     * Создать нового пользователя в бд.
     *
     * @param userDTO объект пользователя
     * @return объект пользователя с идентификатором.
     * @throws UserAlreadyExistException в случае если пользователь уже существует.
     */
    public UserDTO saveUser(UserDTO userDTO) throws UserAlreadyExistException {
        try {
            return this.execute(session -> {
                session.save(userDTO);
                return userDTO;
            });
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistException("User with email " + userDTO.getEmail() + " already exist!");
        }
    }

    /**
     * Найти пользователя по идентификатору
     *
     * @param id идентификатор.
     * @return объект пользователя.
     * @throws UnexistUserException в случае, если пользователя с таким id нету.
     */
    public UserDTO findUserById(int id) throws UnexistUserException {
        UserDTO result = this.execute(session -> session.get(UserDTO.class, id));
        if (result != null) {
            return result;
        } else {
            throw new UnexistUserException("User with id " + id + " not found.");
        }
    }

    /**
     * Найти пользователя по почте.
     *
     * @param email почта.
     * @return объект пользователя.
     * @throws UnexistUserException в случае, когда пользователя с таким email нету.
     */
    public UserDTO findUserByEmail(String email) throws UnexistUserException {
        String query = "from ru.job4j.todo.persistence.models.UserDTO where email=:email";
        UserDTO result = this.execute(session -> (UserDTO) session.createQuery(query).setParameter("email", email).uniqueResult());
        if (result != null) {
            return result;
        } else {
            throw new UnexistUserException("User with that email not found: " + email);
        }
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