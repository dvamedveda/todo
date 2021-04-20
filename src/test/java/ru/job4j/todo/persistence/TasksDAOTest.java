package ru.job4j.todo.persistence;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

import java.sql.Timestamp;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
 * Тесты класса TasksDAO.
 */
public class TasksDAOTest {

    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Тест проверяет успешное добавление задачи в базу данных.
     */
    @Test
    public void whenAddTaskThenSuccess() {
        TasksDAO tasksDAO = new TasksDAO();
        UserDAO userDAO = new UserDAO();
        UserDTO testUser = userDAO.saveUser(new UserDTO("test@test", "password", "test", new Timestamp(1L)));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(0));
        TaskDTO newTask = tasksDAO.addTask(new TaskDTO("test_description", new Timestamp(1L), false, testUser));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(1));
        Assert.assertThat(newTask.getDescription(), is("test_description"));
        Assert.assertThat(newTask.getCreated().getNanos(), is(1000000));
        Assert.assertThat(newTask.isDone(), is(false));
        Assert.assertThat(newTask.getUser(), is(testUser));
        userDAO.deleteUser(testUser);

    }

    /**
     * Тест проверяет изменение задачи в бд.
     */
    @Test
    public void whenUpdateTaskThenSuccess() {
        TasksDAO tasksDAO = new TasksDAO();
        UserDAO userDAO = new UserDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test2@test", "password", "test", new Timestamp(1L)));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(0));
        TaskDTO newTask = tasksDAO.addTask(new TaskDTO("test_description", new Timestamp(1L), false, newUser));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(1));
        newTask.setDescription("modified_description");
        tasksDAO.updateTask(newTask);
        TaskDTO updatedTask = tasksDAO.getTaskById(newTask.getId());
        Assert.assertThat(updatedTask.getDescription(), is("modified_description"));
        Assert.assertThat(updatedTask.getCreated().getNanos(), is(1000000));
        Assert.assertThat(updatedTask.isDone(), is(false));
        Assert.assertEquals(updatedTask.getUser(), newUser);
        userDAO.deleteUser(newUser);
    }

    /**
     * Тест проверяет получение списка всех задач из бд.
     */
    @Test
    public void whenGetAllThenReturnsAll() {
        TasksDAO tasksDAO = new TasksDAO();
        UserDAO userDAO = new UserDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test3@test", "password", "test", new Timestamp(1L)));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(0));
        TaskDTO taskCompleted = tasksDAO.addTask(new TaskDTO("completed task description", new Timestamp(1L), true, newUser));
        TaskDTO taskIncompleted = tasksDAO.addTask(new TaskDTO("incompleted task description", new Timestamp(1L), false, newUser));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(2));
        List<TaskDTO> result = tasksDAO.getAllTasks();
        for (TaskDTO task : result) {
            if (task.getId() == taskCompleted.getId()) {
                Assert.assertThat(task.getDescription(), is(taskCompleted.getDescription()));
                Assert.assertThat(task.getCreated(), is(taskCompleted.getCreated()));
                Assert.assertThat(task.isDone(), is(taskCompleted.isDone()));
                Assert.assertEquals(task.getUser(), newUser);
            } else if (task.getId() == taskIncompleted.getId()) {
                Assert.assertThat(task.getDescription(), is(taskIncompleted.getDescription()));
                Assert.assertThat(task.getCreated(), is(taskIncompleted.getCreated()));
                Assert.assertThat(task.isDone(), is(taskIncompleted.isDone()));
                Assert.assertEquals(task.getUser(), newUser);
            }
        }
        userDAO.deleteUser(newUser);
    }

    /**
     * Тест проверяет получение списка невыполненных задач из бд.
     */
    @Test
    public void whenGetIncompleteThenReturnsUndone() {
        TasksDAO tasksDAO = new TasksDAO();
        UserDAO userDAO = new UserDAO();
        UserDTO newUser = userDAO.saveUser(new UserDTO("test4@test", "password", "test", new Timestamp(1L)));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(0));
        TaskDTO taskCompleted = tasksDAO.addTask(new TaskDTO("test completed task description", new Timestamp(1L), true, newUser));
        TaskDTO taskIncompleted = tasksDAO.addTask(new TaskDTO("test incompleted task description", new Timestamp(1L), false, newUser));
        Assert.assertThat(tasksDAO.getAllTasks().size(), is(2));
        Assert.assertThat(tasksDAO.getIncompleteTasks().size(), is(1));
        List<TaskDTO> result = tasksDAO.getIncompleteTasks();
        TaskDTO resultTask = result.get(0);
        Assert.assertThat(resultTask.getId(), is(taskIncompleted.getId()));
        Assert.assertThat(resultTask.getDescription(), is(taskIncompleted.getDescription()));
        Assert.assertThat(resultTask.getCreated(), is(taskIncompleted.getCreated()));
        Assert.assertThat(resultTask.isDone(), is(taskIncompleted.isDone()));
        Assert.assertEquals(resultTask.getUser(), newUser);
        userDAO.deleteUser(newUser);
    }
}