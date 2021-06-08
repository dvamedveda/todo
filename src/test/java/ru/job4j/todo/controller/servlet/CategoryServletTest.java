package ru.job4j.todo.controller.servlet;

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

import static org.hamcrest.core.Is.is;

/**
 * Тесты класса CategoryServlet.
 */
public class CategoryServletTest {
    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Проверка получения списка всех категорий.
     *
     * @throws Exception исключения при работе сервлета.
     */
    @Test
    public void whenGetCategoriesThenSuccess() throws Exception {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        categoryService.createCategory("Новое");
        Answers answers = new Answers();
        HttpSession session = Mockito.mock(HttpSession.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.doAnswer(answers.new SetAnswer()).when(session).setAttribute(Mockito.anyString(), Mockito.any());
        Mockito.doAnswer(answers.new GetAnswer()).when(session).getAttribute(Mockito.anyString());
        StringWriter testOut = new StringWriter();
        PrintWriter writer = new PrintWriter(testOut);
        Mockito.when(resp.getWriter()).thenReturn(writer);
        List<CategoryDTO> categories = categoryService.getAllCategories();
        new CategoryServlet().doGet(req, resp);
        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append("[");
        for (int i = 0; i < categories.size(); i++) {
            CategoryDTO category = categories.get(i);
            expectedBuilder.append(String.format("{\"id\":%s,\"name\":\"%s\"}", category.getId(), category.getName()));
            if (i < categories.size() - 1) {
                expectedBuilder.append(",");
            }
        }
        expectedBuilder.append("]");
        String expected = expectedBuilder.toString();
        String result = testOut.toString();
        Assert.assertThat(result, is(expected));
        categoryService.removeCategory("Новое");
    }

    /**
     * Получение списка категорий для задачи.
     *
     * @throws Exception исключения при работе сервлета.
     */
    @Test
    public void whenGetTaskCategoriesThenSuccess() throws Exception {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        categoryService.createCategory("New");
        UserService userService = ServiceManager.getInstance().getUserService();
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        CategoryDTO categoryNew = categoryService.getCategoryByName("New");
        CategoryDTO categoryHobby = categoryService.getCategoryByName("Хобби");
        List<Integer> userCategories = new ArrayList<>();
        userCategories.add(categoryNew.getId());
        userCategories.add(categoryHobby.getId());
        UserDTO newUser = userService.addNewUser("for_cat@email", "password", "name");
        TaskDTO newTask = todoService.addNewTask("task with categories", newUser, userCategories);
        Answers answers = new Answers();
        HttpSession session = Mockito.mock(HttpSession.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.doAnswer(answers.new SetAnswer()).when(session).setAttribute(Mockito.anyString(), Mockito.any());
        Mockito.doAnswer(answers.new GetAnswer()).when(session).getAttribute(Mockito.anyString());
        Mockito.when(req.getParameter("task")).thenReturn(Integer.toString(newTask.getId()));
        StringWriter testOut = new StringWriter();
        PrintWriter writer = new PrintWriter(testOut);
        Mockito.when(resp.getWriter()).thenReturn(writer);
        new CategoryServlet().doGet(req, resp);
        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append("[");
        expectedBuilder.append(String.format("{\"id\":%s,\"name\":\"%s\"}", categoryHobby.getId(), categoryHobby.getName()));
        expectedBuilder.append(",");
        expectedBuilder.append(String.format("{\"id\":%s,\"name\":\"%s\"}", categoryNew.getId(), categoryNew.getName()));
        expectedBuilder.append("]");
        String expected = expectedBuilder.toString();
        String result = testOut.toString();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(newTask.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory("New");
    }
}