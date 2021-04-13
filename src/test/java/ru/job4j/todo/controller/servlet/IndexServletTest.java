package ru.job4j.todo.controller.servlet;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Тесты класса IndexServlet.
 */
public class IndexServletTest {

    /**
     * Тест проверяющий перенаправление пользователя на главную страницу
     */
    @Test
    public void whenGetIndexThenSuccess() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(req.getRequestDispatcher("/WEB-INF/view/index.jsp")).thenReturn(dispatcher);
        new IndexServlet().doGet(req, resp);
    }
}