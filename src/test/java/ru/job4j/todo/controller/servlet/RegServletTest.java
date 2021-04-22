package ru.job4j.todo.controller.servlet;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.job4j.todo.controller.Answers;
import ru.job4j.todo.persistence.exceptions.UnexistUserException;
import ru.job4j.todo.persistence.exceptions.UserAlreadyExistException;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.service.ServiceManager;
import ru.job4j.todo.service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;

/**
 * Тесты класса RegServlet.
 */
public class RegServletTest {

    /**
     * Проверка перенаправления на страницу регистрации.
     *
     * @throws IOException      исключения ввода вывода.
     * @throws ServletException исключения при работе сервлета.
     */
    @Test
    public void whenGetThenLoginPage() throws IOException, ServletException {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        RequestDispatcher requestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(req.getRequestDispatcher("/WEB-INF/view/reg.jsp")).thenReturn(requestDispatcher);
        new RegServlet().doGet(req, resp);
        Mockito.verify(requestDispatcher).forward(req, resp);
    }

    /**
     * Проверка регистрации пользователя с валидными данными.
     *
     * @throws IOException      исключения ввода вывода.
     * @throws ServletException исключения при работе сервлета.
     */
    @Test
    public void whenPostValidDataThenLoginPage() throws IOException, ServletException, UnexistUserException {
        UserService userService = ServiceManager.getInstance().getUserService();
        String name = "somename", email = "some@email", password = "somepass";
        Answers answers = new Answers();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        RequestDispatcher requestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.doAnswer(answers.new SetAnswer()).when(req).setAttribute(Mockito.anyString(), Mockito.anyObject());
        Mockito.doAnswer(answers.new GetAnswer()).when(req).getAttribute(Mockito.anyString());
        Mockito.when(req.getParameter("email")).thenReturn(email);
        Mockito.when(req.getParameter("password")).thenReturn(password);
        Mockito.when(req.getParameter("name")).thenReturn(name);
        Mockito.when(req.getRequestDispatcher("/WEB-INF/view/login.jsp")).thenReturn(requestDispatcher);
        new RegServlet().doPost(req, resp);
        Mockito.verify(requestDispatcher).forward(req, resp);
        UserDTO resultUser = userService.getUserByEmail("some@email");
        Assert.assertThat(resultUser.getName(), is(name));
        Assert.assertThat(resultUser.getEmail(), is(email));
        Assert.assertThat(resultUser.getPassword(), is(password));
        String info = (String) req.getAttribute("info");
        Assert.assertThat(info, is("Регистрация прошла успешно. Используйте ранее введенные данные для входа."));
        userService.deleteUser(resultUser);
    }

    /**
     * Проверка регистрации пользователя с существующими данными.
     *
     * @throws IOException      исключения ввода вывода.
     * @throws ServletException исключения при работе сервлета.
     */
    @Test
    public void whenPostExistDataThenRegPage() throws IOException, ServletException, UserAlreadyExistException {
        UserService userService = ServiceManager.getInstance().getUserService();
        UserDTO newUser = userService.addNewUser("some@email", "somepass", "somename");
        String name = "somename", email = "some@email", password = "somepass";
        Answers answers = new Answers();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        RequestDispatcher requestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.doAnswer(answers.new SetAnswer()).when(req).setAttribute(Mockito.anyString(), Mockito.anyObject());
        Mockito.doAnswer(answers.new GetAnswer()).when(req).getAttribute(Mockito.anyString());
        Mockito.when(req.getParameter("email")).thenReturn(email);
        Mockito.when(req.getParameter("password")).thenReturn(password);
        Mockito.when(req.getParameter("name")).thenReturn(name);
        Mockito.when(req.getRequestDispatcher("/WEB-INF/view/reg.jsp")).thenReturn(requestDispatcher);
        new RegServlet().doPost(req, resp);
        Mockito.verify(requestDispatcher).forward(req, resp);
        String info = (String) req.getAttribute("error");
        Assert.assertThat(info, is("Пользователь с таким email уже существует!"));
        userService.deleteUser(newUser);
    }

    /**
     * Проверка регистрации пользователя с невалидными данными.
     *
     * @throws IOException      исключения ввода вывода.
     * @throws ServletException исключения при работе сервлета.
     */
    @Test
    public void whenPostEmptyDataThenRegPage() throws IOException, ServletException {
        String name = "", email = "", password = "";
        Answers answers = new Answers();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        RequestDispatcher requestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.doAnswer(answers.new SetAnswer()).when(req).setAttribute(Mockito.anyString(), Mockito.anyObject());
        Mockito.doAnswer(answers.new GetAnswer()).when(req).getAttribute(Mockito.anyString());
        Mockito.when(req.getParameter("email")).thenReturn(email);
        Mockito.when(req.getParameter("password")).thenReturn(password);
        Mockito.when(req.getParameter("name")).thenReturn(name);
        Mockito.when(req.getRequestDispatcher("/WEB-INF/view/reg.jsp")).thenReturn(requestDispatcher);
        new RegServlet().doPost(req, resp);
        Mockito.verify(requestDispatcher).forward(req, resp);
        String info = (String) req.getAttribute("error");
        Assert.assertThat(info, is("Нужно заполнить все поля."));
    }
}