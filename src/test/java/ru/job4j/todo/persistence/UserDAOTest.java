package ru.job4j.todo.persistence;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

import java.sql.Timestamp;

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
    public void whenAddUserThenSuccess() {
        UserDAO userDAO = new UserDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test11@test", "password", "test", new Timestamp(10L)));
        UserDTO resultUser = userDAO.findUserById(newUser.getId());
        Assert.assertEquals(resultUser, newUser);
        userDAO.deleteUser(resultUser);
    }

    /**
     * Поиск пользователя по почте.
     */
    @Test
    public void whenSearchUserByEmailThenSuccess() {
        UserDAO userDAO = new UserDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test22@test", "password", "test", new Timestamp(22L)));
        UserDTO resultUser = userDAO.findUserByEmail(newUser.getEmail());
        Assert.assertEquals(resultUser, newUser);
        userDAO.deleteUser(resultUser);
    }

    /**
     * Проверка удаления задач пользователя при удалении пользователя.
     */
    @Test
    public void whenDeleteUserThenTasksDeletedToo() {
        UserDAO userDAO = new UserDAO();
        TasksDAO tasksDAO = new TasksDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test33@test", "password", "test", new Timestamp(33L)));
        Assert.assertThat(tasksDAO.getUserAllTasks(newUser.getId()).size(), is(0));
        tasksDAO.addTask(new TaskDTO("completed task description", new Timestamp(1L), true, newUser));
        tasksDAO.addTask(new TaskDTO("incompleted task description", new Timestamp(1L), false, newUser));
        Assert.assertThat(tasksDAO.getUserAllTasks(newUser.getId()).size(), is(2));
        userDAO.deleteUser(newUser);
        Assert.assertThat(tasksDAO.getUserAllTasks(newUser.getId()).size(), is(0));
        Assert.assertNull(userDAO.findUserById(newUser.getId()));
    }
}