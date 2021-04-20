package ru.job4j.todo.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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
    public void whenAddUserThenSuccess() {
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
    public void whenCheckExistUserThenTrue() {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some1@email", "somePass", "someName");
        Assert.assertTrue(userService.checkExistUser("some1@email"));
        userService.deleteUser(newUser);
    }

    /**
     * Проверка существования пользователя, если пользователь не существует.
     */
    @Test
    public void whenCheckUnexistUserThenFalse() {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some1@email", "somePass", "someName");
        Assert.assertFalse(userService.checkExistUser("some111@email"));
        userService.deleteUser(newUser);
    }

    /**
     * Полученеи объекта пользователя по идентификатору.
     */
    @Test
    public void whenGetUserByIdThenCorrect() {
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
    public void whenGetUserByEmailThenCorrect() {
        UserService userService = new UserService();
        UserDTO newUser = userService.addNewUser("some3@email", "somePass", "someName");
        UserDTO resultUser = userService.getUserByEmail(newUser.getEmail());
        Assert.assertEquals(newUser, resultUser);
        userService.deleteUser(newUser);
    }
}