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
import java.io.PrintWriter;

/**
 * Сервлет для работы со списком задач.
 */
public class TodoServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TodoService service = ServiceManager.getInstance().getTodoService();

    /**
     * Получение списка задач из сервиса.
     *
     * @param req  объект запроса.
     * @param resp объект ответа.
     * @throws ServletException исключения при работе сервлета.
     * @throws IOException      исключения при работе с сервис слоем.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String taskList;
        if (req.getParameter("completed").equals("true")) {
            LOGGER.info("Requested list of all tasks from " + req.getRemoteAddr() + ":" + req.getRemotePort());
            taskList = service.getAllTasksAsJson();
        } else {
            LOGGER.info("Requested list of incompleted tasks from " + req.getRemoteAddr() + ":" + req.getRemotePort());
            taskList = service.getIncompleteTasksAsJson();
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/json");
        try (PrintWriter out = resp.getWriter()) {
            LOGGER.info("Send task list to " + req.getRemoteAddr() + ":" + req.getRemotePort());
            out.write(taskList);
        } catch (Exception e) {
            LOGGER.info(e, e);
        }
    }

    /**
     * Добавление новой задачи в список.
     *
     * @param req  объект запроса.
     * @param resp объект ответа.
     * @throws ServletException исключения при работе сервлета.
     * @throws IOException      исключения при работе с сервис слоем.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String description = req.getParameter("description");
        LOGGER.info("Creating new task with description \"" + description + "\"");
        service.addNewTask(description);
    }
}