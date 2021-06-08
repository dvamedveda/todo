package ru.job4j.todo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.todo.persistence.CategoryDAO;
import ru.job4j.todo.persistence.TasksDAO;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;

import java.sql.Timestamp;
import java.util.List;

/**
 * Класс реализующий слой сервиса для todo-сервиса.
 */
public class TodoService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TasksDAO tasksDAO = new TasksDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    /**
     * Получение всех задач в сервисе.
     *
     * @return строка в формате json.
     */
    public String getAllTasksAsJson() {
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        List tasks = tasksDAO.getAllTasks();
        try {
            result = mapper.writeValueAsString(tasks);
        } catch (Exception e) {
            LOGGER.info(e, e);
        }
        return result;
    }

    /**
     * Получение незавершенных задач в сервисе.
     *
     * @return строка в формате json.
     */
    public String getIncompleteTasksAsJson() {
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        List tasks = tasksDAO.getIncompleteTasks();
        try {
            result = mapper.writeValueAsString(tasks);
        } catch (Exception e) {
            LOGGER.info(e, e);
        }
        return result;
    }

    /**
     * Добавление новой задачи.
     *
     * @param description описание новой задачи.
     * @return объект созданной задачи.
     */
    public TaskDTO addNewTask(String description, UserDTO user, List<Integer> categories) {
        TaskDTO task = new TaskDTO(description, new Timestamp(System.currentTimeMillis()), false, user);
        for (Integer id : categories) {
            task.addCategory(categoryDAO.getCategoryById(id));
        }
        return tasksDAO.addTask(task);
    }

    /**
     * Изменить флаг выполнения задачи.
     *
     * @param id    идентификатор задачи.
     * @param state новое состояние флага.
     */
    public void checkTask(int id, boolean state) {
        TaskDTO task = tasksDAO.getTaskById(id);
        if (task != null) {
            task.setDone(state);
            tasksDAO.updateTask(task);
        }
    }

    /**
     * Удаление задачи по идентификатору.
     *
     * @param id идентификатор.
     */
    public void deleteTask(int id) {
        TaskDTO task = tasksDAO.getTaskById(id);
        tasksDAO.deleteTask(task);
    }

    /**
     * Получение объекта задачи по идентификатору.
     *
     * @param id идентификатор.
     * @return объект задачи.
     */
    public TaskDTO getTaskById(int id) {
        return tasksDAO.getTaskById(id);
    }

    /**
     * Удалить задачи пользователя.
     *
     * @param id идентификатор пользователя.
     */
    public void deleteUserTasks(int id) {
        tasksDAO.deleteUserTasks(id);
    }


}