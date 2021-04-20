package ru.job4j.todo.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

import static org.hamcrest.CoreMatchers.is;

/**
 * Тесты класса TodoService.
 */
public class TodoServiceTest {

    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Проверка добавления задачи на слое сервиса.
     */
    @Test
    public void whenAddTaskThenSuccess() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        UserDTO newUser = userService.addNewUser("test1@test1", "password", "some_name");
        TaskDTO task = todoService.addNewTask("some test task", newUser);
        TaskDTO result = todoService.getTaskById(task.getId());
        Assert.assertThat(result.getId(), is(task.getId()));
        Assert.assertThat(result.getDescription(), is("some test task"));
        Assert.assertThat(result.getCreated(), is(task.getCreated()));
        Assert.assertThat(result.isDone(), is(false));
        Assert.assertEquals(result.getUser(), newUser);
        todoService.deleteTask(result.getId());
        userService.deleteUser(newUser);
    }

    /**
     * Проверка изменения статуса задачи на слое сервиса.
     */
    @Test
    public void whenCheckTaskThenSuccess() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        UserDTO newUser = userService.addNewUser("test2@test2", "password", "some_name");
        TaskDTO task = todoService.addNewTask("new test task", newUser);
        todoService.checkTask(task.getId(), true);
        TaskDTO result = todoService.getTaskById(task.getId());
        Assert.assertThat(result.getId(), is(task.getId()));
        Assert.assertThat(result.getDescription(), is("new test task"));
        Assert.assertThat(result.getCreated(), is(task.getCreated()));
        Assert.assertThat(result.isDone(), is(true));
        Assert.assertEquals(result.getUser(), newUser);
        todoService.deleteTask(result.getId());
        userService.deleteUser(newUser);
    }

    /**
     * Проверка получения списка всех задача на слое сервиса.
     */
    @Test
    public void whenGetAllTasksThenCorrect() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        UserDTO newUser = userService.addNewUser("test3@test3", "password", "some_name");
        TaskDTO taskCompleted = todoService.addNewTask("completed task", newUser);
        TaskDTO taskIncompleted = todoService.addNewTask("incompleted task", newUser);
        todoService.checkTask(taskCompleted.getId(), true);
        int completedId = taskCompleted.getId();
        long completedTime = taskCompleted.getCreated().getTime();
        int incompletedId = taskIncompleted.getId();
        long incompletedTime = taskIncompleted.getCreated().getTime();
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"incompleted task\",", incompletedId))
                .append(String.format("\"created\":%s,\"done\":false,", incompletedTime))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s}}", newUser.getRegistered().getTime()))
                .append(",")
                .append(String.format("{\"id\":%s,\"description\":\"completed task\",", completedId))
                .append(String.format("\"created\":%s,\"done\":true,", completedTime))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s}}", newUser.getRegistered().getTime()))
                .append("]")
                .toString();
        String result = todoService.getAllTasksAsJson();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(taskCompleted.getId());
        todoService.deleteTask(taskIncompleted.getId());
        userService.deleteUser(newUser);
    }

    /**
     * Проверка получения списка незавершенных задач на слое сервиса.
     */
    @Test
    public void whenGetIncompletedTasksThenCorrect() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        UserDTO newUser = userService.addNewUser("test4@test4", "password", "some_name");
        TaskDTO taskCompleted = todoService.addNewTask("test completed task", newUser);
        TaskDTO taskIncompleted = todoService.addNewTask("test incompleted task", newUser);
        todoService.checkTask(taskCompleted.getId(), true);
        int incompletedId = taskIncompleted.getId();
        long incompletedTime = taskIncompleted.getCreated().getTime();
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"test incompleted task\",", incompletedId))
                .append(String.format("\"created\":%s,\"done\":false,", incompletedTime))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s}}", newUser.getRegistered().getTime()))
                .append("]")
                .toString();
        String result = todoService.getIncompleteTasksAsJson();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(taskCompleted.getId());
        todoService.deleteTask(taskIncompleted.getId());
        userService.deleteUser(newUser);
    }

    /**
     * Проверка удаления тасков пользователя.
     */
    @Test
    public void whenDeleteUserTasksThenSuccess() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        UserDTO newUser = userService.addNewUser("test4@test4", "password", "some_name");
        todoService.addNewTask("test completed task1", newUser);
        todoService.addNewTask("test incompleted task2", newUser);
        todoService.deleteUserTasks(newUser.getId());
        userService.deleteUser(newUser);
        Assert.assertThat(todoService.getAllTasksAsJson(), is("[]"));
    }
}