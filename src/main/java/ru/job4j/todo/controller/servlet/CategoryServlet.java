package ru.job4j.todo.controller.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.ServiceManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Сервлет для получения списка категорий.
 */
public class CategoryServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CategoryService service = ServiceManager.getInstance().getCategoryService();

    /**
     * Получение списка всех категорий или списка категорий для задачи, если указан параметр.
     *
     * @param req  объект запроса.
     * @param resp объект ответа.
     * @throws ServletException исключения при работе сервлета.
     * @throws IOException      исключения ввода и вывода.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/json");
        String allCategories;
        if (req.getParameter("task") != null) {
            int taskId = Integer.parseInt(req.getParameter("task"));
            allCategories = service.getTaskCategoriesAsJson(taskId);
        } else {
            allCategories = service.getAllCategoriesAsJson();
        }
        try (PrintWriter out = resp.getWriter()) {
            LOGGER.info("Send category list to " + req.getRemoteAddr() + ":" + req.getRemotePort());
            out.write(allCategories);
        } catch (Exception e) {
            LOGGER.info(e, e);
        }
    }
}