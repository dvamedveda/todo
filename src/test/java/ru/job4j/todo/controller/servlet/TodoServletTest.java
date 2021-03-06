package ru.job4j.todo.controller.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import ru.job4j.todo.controller.Answers;
import ru.job4j.todo.persistence.models.CategoryDTO;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.ServiceManager;
import ru.job4j.todo.service.TodoService;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        UserDTO newUser = userService.addNewUser("som2@email", "password", "name");
        Answers answers = new Answers();
        HttpSession session = Mockito.mock(HttpSession.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.doAnswer(answers.new SetAnswer()).when(session).setAttribute(Mockito.anyString(), Mockito.any());
        Mockito.doAnswer(answers.new GetAnswer()).when(session).getAttribute(Mockito.anyString());
        Mockito.when(req.getParameter("description")).thenReturn("task added from controller");
        Mockito.when(req.getParameter("categoryIds")).thenReturn(String.format("[\"%s\"]", category.getId()));
        Mockito.when(req.getSession()).thenReturn(session);
        Mockito.when(session.getAttribute("user")).thenReturn(newUser);
        Assert.assertThat(todoService.getAllTasksAsJson(), is("[]"));
        new TodoServlet().doPost(req, resp);
        String result = todoService.getAllTasksAsJson();
        ObjectMapper mapper = new ObjectMapper();
        TaskDTO[] resultArray = mapper.readValue(result, TaskDTO[].class);
        TaskDTO resultTask = resultArray[0];
        Assert.assertThat(resultTask.getDescription(), is("task added from controller"));
        Assert.assertThat(resultTask.isDone(), is(false));
        Assert.assertEquals(resultTask.getUser(), newUser);
        Assert.assertThat(resultTask.getCategoryDTOList().size(), is(1));
        Assert.assertEquals(resultTask.getCategoryDTOList().get(0), category);
        todoService.deleteTask(resultTask.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
    }

    /**
     * Тест проверяет успешное получение списка всех задач в контроллере.
     *
     * @throws Exception исключения при работе теста.
     */
    @Test
    public void whenGetAllTasksThenSuccess() throws Exception {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        List<Integer> categoryList = new ArrayList<>();
        categoryList.add(category.getId());
        UserDTO newUser = userService.addNewUser("som3@email", "password", "name");
        TaskDTO completedTask = todoService.addNewTask("completed from controller", newUser, categoryList);
        TaskDTO incompletedTask = todoService.addNewTask("incompleted from controller", newUser, categoryList);
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
                .append(String.format("\"created\":%s,\"done\":false,", incompletedTask.getCreated().getTime()))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s},", newUser.getRegistered().getTime()))
                .append(String.format("\"categoryDTOList\":[{\"id\":%s,\"name\":\"%s\"}]}", category.getId(), category.getName()))
                .append(",")
                .append(String.format("{\"id\":%s,\"description\":\"completed from controller\",", completedTask.getId()))
                .append(String.format("\"created\":%s,\"done\":true,", completedTask.getCreated().getTime()))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s},", newUser.getRegistered().getTime()))
                .append(String.format("\"categoryDTOList\":[{\"id\":%s,\"name\":\"%s\"}]}", category.getId(), category.getName()))
                .append("]")
                .toString();
        String result = testOut.toString();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(completedTask.getId());
        todoService.deleteTask(incompletedTask.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
    }

    /**
     * Тест проверяет успешное получение списка невыполненных задач в контроллере.
     *
     * @throws Exception исключения при работе теста.
     */
    @Test
    public void whenGetIncompletedTasksThenSuccess() throws Exception {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        List<Integer> categoryList = new ArrayList<>();
        categoryList.add(category.getId());
        UserDTO newUser = userService.addNewUser("som4@email", "password", "name");
        TaskDTO completedTask = todoService.addNewTask("completed from controller", newUser, categoryList);
        TaskDTO incompletedTask = todoService.addNewTask("incompleted from controller", newUser, categoryList);
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
                .append(String.format("\"created\":%s,\"done\":false,", incompletedTask.getCreated().getTime()))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s},", newUser.getRegistered().getTime()))
                .append(String.format("\"categoryDTOList\":[{\"id\":%s,\"name\":\"%s\"}]}", category.getId(), category.getName()))
                .append("]")
                .toString();
        String result = testOut.toString();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(completedTask.getId());
        todoService.deleteTask(incompletedTask.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
    }
}