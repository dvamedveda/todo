package ru.job4j.todo.controller.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.service.ServiceManager;
import ru.job4j.todo.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Сервлет для авторизации пользователей.
 */
public class AuthServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UserService service = ServiceManager.getInstance().getUserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String errorMessage = "";
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if (service.checkExistUser(email)) {
            UserDTO user = service.getUserByEmail(email);
            if (user.getPassword().equals(password)) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                LOGGER.info("User " + user.getName() + " logged into service.");
                resp.sendRedirect(req.getContextPath() + "/index.do");
            } else {
                LOGGER.info("User " + user.getName() + " trying to log in to service with invalid password.");
                errorMessage = "Неверный пароль!";
                req.setAttribute("error", errorMessage);
                req.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(req, resp);
            }
        } else {
            LOGGER.info("User trying to log in to service with invalid email " + email + ".");
            errorMessage = "Пользователя с таким email не существует!";
            req.setAttribute("error", errorMessage);
            req.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(req, resp);
    }
}