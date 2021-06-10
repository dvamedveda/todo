package ru.job4j.todo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.todo.persistence.TasksDAO;
import ru.job4j.todo.persistence.UserDAO;
import ru.job4j.todo.persistence.exceptions.UnexistUserException;
import ru.job4j.todo.persistence.exceptions.UserAlreadyExistException;
import ru.job4j.todo.persistence.models.UserDTO;

import java.sql.Date;

/**
 * Класс сервиса для работы с пользователями.
 */
public class UserService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TasksDAO tasksDAO = new TasksDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Добавить нового пользователя.
     *
     * @param email    почта.
     * @param password пароль.
     * @param name     имя.
     * @return объект созданного пользователя.
     * @throws UserAlreadyExistException в случае, когда такой пользователь уже существует.
     */
    public UserDTO addNewUser(String email, String password, String name) throws UserAlreadyExistException {
        return userDAO.saveUser(new UserDTO(email, password, name, new Date(System.currentTimeMillis())));
    }

    /**
     * Получить пользователя по идентификатору.
     *
     * @param id идентификатор.
     * @return объект пользователя.
     * @throws UnexistUserException в случае, если пользователя с таким идентификатором не существует.
     */
    public UserDTO getUserById(int id) throws UnexistUserException {
        return userDAO.findUserById(id);
    }

    /**
     * Получить пользователя по почте.
     *
     * @param email почта.
     * @return объект пользователя.
     * @throws UnexistUserException в случае, если пользователя с такой почтой не существует.
     */
    public UserDTO getUserByEmail(String email) throws UnexistUserException {
        return userDAO.findUserByEmail(email);
    }

    /**
     * Удалить пользователя.
     *
     * @param userDTO объект пользователя.
     */
    public void deleteUser(UserDTO userDTO) {
        userDAO.deleteUser(userDTO);
    }
}