package ru.job4j.todo.persistence;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.exceptions.UnexistUserException;
import ru.job4j.todo.persistence.exceptions.UserAlreadyExistException;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;

/**
 * Тесты класса UserDAO.
 */
public class UserDAOTest {
    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Добавление нового пользователя.
     */
    @Test
    public void whenAddUserThenSuccess() throws UserAlreadyExistException, UnexistUserException {
        UserDAO userDAO = new UserDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test11@test", "password", "test", new Date(10L)));
        UserDTO resultUser = userDAO.findUserById(newUser.getId());
        Assert.assertEquals(resultUser, newUser);
        userDAO.deleteUser(resultUser);
    }

    /**
     * Поиск пользователя по почте.
     */
    @Test
    public void whenSearchUserByEmailThenSuccess()throws UserAlreadyExistException, UnexistUserException {
        UserDAO userDAO = new UserDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test22@test", "password", "test", new Date(22L)));
        UserDTO resultUser = userDAO.findUserByEmail(newUser.getEmail());
        Assert.assertEquals(resultUser, newUser);
        userDAO.deleteUser(resultUser);
    }

    /**
     * Проверка удаления задач пользователя при удалении пользователя.
     */
    @Test(expected = UnexistUserException.class)
    public void whenDeleteUserThenTasksDeletedToo() throws UserAlreadyExistException, UnexistUserException {
        UserDAO userDAO = new UserDAO();
        TasksDAO tasksDAO = new TasksDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test33@test", "password", "test", new Date(33L)));
        Assert.assertThat(tasksDAO.getUserAllTasks(newUser.getId()).size(), is(0));
        tasksDAO.addTask(new TaskDTO("completed task description", new Date(1L), true, newUser));
        tasksDAO.addTask(new TaskDTO("incompleted task description", new Date(1L), false, newUser));
        Assert.assertThat(tasksDAO.getUserAllTasks(newUser.getId()).size(), is(2));
        userDAO.deleteUser(newUser);
        Assert.assertThat(tasksDAO.getUserAllTasks(newUser.getId()).size(), is(0));
        userDAO.findUserById(newUser.getId());
    }
}