package ru.job4j.todo.controller.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.todo.service.ServiceManager;
import ru.job4j.todo.service.TodoService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет для изменения параметров задачи.
 */
public class TaskServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TodoService service = ServiceManager.getInstance().getTodoService();

    /**
     * По идентификатору меняем состояние задачи.
     *
     * @param req  запрос с параметрами
     * @param resp ответ на запрос
     * @throws ServletException исключения сервлета.
     * @throws IOException      исключения при работе со слоем сервиса.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean taskState;
        int id = Integer.parseInt(req.getParameter("id"));
        String state = req.getParameter("state");
        if (state.equals("true")) {
            taskState = true;
        } else {
            taskState = false;
        }
        LOGGER.info("Task with id " + id + " checked as " + (taskState ? "done" : "incompleted") + ".");
        service.checkTask(id, taskState);
    }
}