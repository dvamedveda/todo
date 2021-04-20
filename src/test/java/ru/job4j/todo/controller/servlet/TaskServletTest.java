package ru.job4j.todo.controller.servlet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;
import ru.job4j.todo.service.ServiceManager;
import ru.job4j.todo.service.TodoService;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;

/**
 * Тесты класса TaskServlet.
 */
public class TaskServletTest {

    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Тест, проверяющий успешное изменение задачи.
     *
     * @throws Exception исключения при работе тестов.
     */
    @Test
    public void whenCheckTaskThenSuccess() throws Exception {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        UserDTO newUser = userService.addNewUser("som1@email", "password", "name");
        TaskDTO task = todoService.addNewTask("new task from controller", newUser);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("id")).thenReturn(Integer.toString(task.getId()));
        Mockito.when(req.getParameter("state")).thenReturn(Boolean.toString(true));
        new TaskServlet().doPost(req, resp);
        TaskDTO result = todoService.getTaskById(task.getId());
        Assert.assertThat(result.getDescription(), is(task.getDescription()));
        Assert.assertThat(result.getCreated(), is(task.getCreated()));
        Assert.assertThat(result.isDone(), is(true));
        todoService.deleteTask(result.getId());
        userService.deleteUser(newUser);
    }
}