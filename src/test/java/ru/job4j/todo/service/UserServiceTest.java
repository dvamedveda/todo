package ru.job4j.todo.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.exceptions.UnexistUserException;
import ru.job4j.todo.persistence.exceptions.UserAlreadyExistException;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

/**
 * Тесты класса UserService.
 */
public class UserServiceTest {
    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Проверка создания нового пользователя.
     */
    @Test
    public void whenAddUserThenSuccess() throws UserAlreadyExistException, UnexistUserException {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some@email", "somePass", "someName");
        UserDTO resultUser = userService.getUserByEmail("some@email");
        Assert.assertEquals(newUser, resultUser);
        userService.deleteUser(resultUser);
    }

    /**
     * Проверка существования пользователя, если пользователь существует.
     */
    @Test
    public void whenCheckExistUserThenTrue() throws UserAlreadyExistException, UnexistUserException {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some1@email", "somePass", "someName");
        Assert.assertEquals(userService.getUserByEmail("some1@email"), newUser);
        userService.deleteUser(newUser);
    }

    /**
     * Проверка существования пользователя, если пользователь не существует.
     */
    @Test(expected = UnexistUserException.class)
    public void whenCheckUnexistUserThenFalse() throws UserAlreadyExistException, UnexistUserException {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some1@email", "somePass", "someName");
        try {
            userService.getUserByEmail("some111@email");
        } finally {
            userService.deleteUser(newUser);
        }
    }

    /**
     * Полученеи объекта пользователя по идентификатору.
     */
    @Test
    public void whenGetUserByIdThenCorrect() throws UserAlreadyExistException, UnexistUserException {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some2@email", "somePass", "someName");
        UserDTO resultUser = userService.getUserById(newUser.getId());
        Assert.assertEquals(newUser, resultUser);
        userService.deleteUser(newUser);
    }

    /**
     * Получение объекта пользователя по почте.
     */
    @Test
    public void whenGetUserByEmailThenCorrect() throws UserAlreadyExistException, UnexistUserException {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some3@email", "somePass", "someName");
        UserDTO resultUser = userService.getUserByEmail(newUser.getEmail());
        Assert.assertEquals(newUser, resultUser);
        userService.deleteUser(newUser);
    }
}