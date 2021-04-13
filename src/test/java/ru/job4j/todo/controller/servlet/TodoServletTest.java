package ru.job4j.todo.controller.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;
import ru.job4j.todo.service.ServiceManager;
import ru.job4j.todo.service.TodoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;


/**
 * Тесты класса TodoServlet.
 */
public class TodoServletTest {

    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Тест проверяет успешно добавление задачи в контроллере.
     *
     * @throws Exception исключения при работе теста.
     */
    @Test
    public void whenAddTaskThenSuccess() throws Exception {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("description")).thenReturn("task added from controller");
        Assert.assertThat(todoService.getAllTasksAsJson(), is("[]"));
        new TodoServlet().doPost(req, resp);
        String result = todoService.getAllTasksAsJson();
        ObjectMapper mapper = new ObjectMapper();
        TaskDTO[] resultArray = mapper.readValue(result, TaskDTO[].class);
        TaskDTO resultTask = resultArray[0];
        Assert.assertThat(resultTask.getDescription(), is("task added from controller"));
        Assert.assertThat(resultTask.isDone(), is(false));
        todoService.deleteTask(resultTask.getId());
    }

    /**
     * Тест проверяет успешное получение списка всех задач в контроллере.
     *
     * @throws Exception исключения при работе теста.
     */
    @Test
    public void whenGetAllTasksThenSuccess() throws Exception {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        TaskDTO completedTask = todoService.addNewTask("completed from controller");
        TaskDTO incompletedTask = todoService.addNewTask("incompleted from controller");
        todoService.checkTask(completedTask.getId(), true);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("completed")).thenReturn(Boolean.toString(true));
        Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(req.getRemotePort()).thenReturn(9999);
        StringWriter testOut = new StringWriter();
        PrintWriter writer = new PrintWriter(testOut);
        Mockito.when(resp.getWriter()).thenReturn(writer);
        new TodoServlet().doGet(req, resp);
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"incompleted from controller\",", incompletedTask.getId()))
                .append(String.format("\"created\":%s,\"done\":false}", incompletedTask.getCreated().getTime()))
                .append(",")
                .append(String.format("{\"id\":%s,\"description\":\"completed from controller\",", completedTask.getId()))
                .append(String.format("\"created\":%s,\"done\":true}", completedTask.getCreated().getTime()))
                .append("]")
                .toString();
        String result = testOut.toString();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(completedTask.getId());
        todoService.deleteTask(incompletedTask.getId());
    }

    /**
     * Тест проверяет успешное получение списка невыполненных задач в контроллере.
     *
     * @throws Exception исключения при работе теста.
     */
    @Test
    public void whenGetIncompletedTasksThenSuccess() throws Exception {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        TaskDTO completedTask = todoService.addNewTask("completed from controller");
        TaskDTO incompletedTask = todoService.addNewTask("incompleted from controller");
        todoService.checkTask(completedTask.getId(), true);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("completed")).thenReturn(Boolean.toString(false));
        Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(req.getRemotePort()).thenReturn(9999);
        StringWriter testOut = new StringWriter();
        PrintWriter writer = new PrintWriter(testOut);
        Mockito.when(resp.getWriter()).thenReturn(writer);
        new TodoServlet().doGet(req, resp);
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"incompleted from controller\",", incompletedTask.getId()))
                .append(String.format("\"created\":%s,\"done\":false}", incompletedTask.getCreated().getTime()))
                .append("]")
                .toString();
        String result = testOut.toString();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(completedTask.getId());
        todoService.deleteTask(incompletedTask.getId());
    }
}