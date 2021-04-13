package ru.job4j.todo.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.models.TaskDTO;
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
        TaskDTO task = todoService.addNewTask("some test task");
        TaskDTO result = todoService.getTaskById(task.getId());
        Assert.assertThat(result.getId(), is(task.getId()));
        Assert.assertThat(result.getDescription(), is("some test task"));
        Assert.assertThat(result.getCreated(), is(task.getCreated()));
        Assert.assertThat(result.isDone(), is(false));
        todoService.deleteTask(result.getId());
    }

    /**
     * Проверка изменения статуса задачи на слое сервиса.
     */
    @Test
    public void whenCheckTaskThenSuccess() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        TaskDTO task = todoService.addNewTask("new test task");
        todoService.checkTask(task.getId(), true);
        TaskDTO result = todoService.getTaskById(task.getId());
        Assert.assertThat(result.getId(), is(task.getId()));
        Assert.assertThat(result.getDescription(), is("new test task"));
        Assert.assertThat(result.getCreated(), is(task.getCreated()));
        Assert.assertThat(result.isDone(), is(true));
        todoService.deleteTask(result.getId());
    }

    /**
     * Проверка получения списка всех задача на слое сервиса.
     */
    @Test
    public void whenGetAllTasksThenCorrect() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        TaskDTO taskCompleted = todoService.addNewTask("completed task");
        TaskDTO taskIncompleted = todoService.addNewTask("incompleted task");
        todoService.checkTask(taskCompleted.getId(), true);
        int completedId = taskCompleted.getId();
        long completedTime = taskCompleted.getCreated().getTime();
        int incompletedId = taskIncompleted.getId();
        long incompletedTime = taskIncompleted.getCreated().getTime();
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"incompleted task\",", incompletedId))
                .append(String.format("\"created\":%s,\"done\":false}", incompletedTime))
                .append(",")
                .append(String.format("{\"id\":%s,\"description\":\"completed task\",", completedId))
                .append(String.format("\"created\":%s,\"done\":true}", completedTime))
                .append("]")
                .toString();
        String result = todoService.getAllTasksAsJson();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(taskCompleted.getId());
        todoService.deleteTask(taskIncompleted.getId());
    }

    /**
     * Проверка получения списка незавершенных задач на слое сервиса.
     */
    @Test
    public void whenGetIncompletedTasksThenCorrect() {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        TaskDTO taskCompleted = todoService.addNewTask("test completed task");
        TaskDTO taskIncompleted = todoService.addNewTask("test incompleted task");
        todoService.checkTask(taskCompleted.getId(), true);
        int incompletedId = taskIncompleted.getId();
        long incompletedTime = taskIncompleted.getCreated().getTime();
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"test incompleted task\",", incompletedId))
                .append(String.format("\"created\":%s,\"done\":false}", incompletedTime))
                .append("]")
                .toString();
        String result = todoService.getIncompleteTasksAsJson();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(taskCompleted.getId());
        todoService.deleteTask(taskIncompleted.getId());
    }
}