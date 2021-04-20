package ru.job4j.todo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.todo.persistence.TasksDAO;
import ru.job4j.todo.persistence.UserDAO;
import ru.job4j.todo.persistence.models.UserDTO;

import java.sql.Timestamp;

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
     * @param email    почта пользователя.
     * @param password пароль пользователя.
     * @param name     имя пользователя.
     * @return объект пользователя.
     */
    public UserDTO addNewUser(String email, String password, String name) {
        return userDAO.saveUser(new UserDTO(email, password, name, new Timestamp(System.currentTimeMillis())));
    }

    /**
     * Проверить по почте существование пользователя в приложении.
     *
     * @param email почта пользователя.
     * @return пользователь существует или нет.
     */
    public boolean checkExistUser(String email) {
        boolean result = true;
        if (userDAO.findUserByEmail(email) == null) {
            result = false;
        }
        return result;
    }

    /**
     * Получить объект пользователя по идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект пользователя.
     */
    public UserDTO getUserById(int id) {
        return userDAO.findUserById(id);
    }

    /**
     * Получить объект пользователя по почте.
     *
     * @param email почта пользователя.
     * @return объект пользователя.
     */
    public UserDTO getUserByEmail(String email) {
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