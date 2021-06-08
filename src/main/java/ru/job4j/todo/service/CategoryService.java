package ru.job4j.todo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.todo.persistence.CategoryDAO;
import ru.job4j.todo.persistence.TasksDAO;
import ru.job4j.todo.persistence.exceptions.UnexistCategoryException;
import ru.job4j.todo.persistence.models.CategoryDTO;
import ru.job4j.todo.persistence.models.TaskDTO;

import java.util.List;

/**
 * Класс, реализующий слой сервиса для работы с категориями.
 */
public class CategoryService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TasksDAO tasksDAO = new TasksDAO();

    /**
     * Создать новую категорию.
     *
     * @param name название категории.
     * @return объект созданной категории.
     */
    public CategoryDTO createCategory(String name) {
        CategoryDTO category = new CategoryDTO(name);
        return categoryDAO.addCategory(category);
    }

    /**
     * Получить категорию по идентификатору.
     *
     * @param id идентификатор.
     * @return объект категории.
     */
    public CategoryDTO getCategoryById(int id) {
        return this.categoryDAO.getCategoryById(id);
    }

    /**
     * Получить категорию по названию.
     *
     * @param name название категории.
     * @return объект категории.
     */
    public CategoryDTO getCategoryByName(String name) {
        CategoryDTO result = null;
        try {
            result = this.categoryDAO.getCategoryByName(name);
        } catch (UnexistCategoryException e) {
            LOGGER.warn(e, e);
        }
        return result;
    }

    /**
     * Получить список категорий задачи.
     *
     * @param taskDTO объект задачи.
     * @return список категорий.
     */
    public List<CategoryDTO> getTaskCategories(TaskDTO taskDTO) {
        return this.categoryDAO.getTaskCategories(taskDTO);
    }

    /**
     * Получить список всех существующих категорий.
     *
     * @return список категорий.
     */
    public List<CategoryDTO> getAllCategories() {
        return this.categoryDAO.getAllCategories();
    }

    /**
     * Получить все категории в виде JSON.
     *
     * @return строка в формате JSON.
     */
    public String getAllCategoriesAsJson() {
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        List<CategoryDTO> categories = getAllCategories();
        try {
            result = mapper.writeValueAsString(categories);
        } catch (Exception e) {
            LOGGER.info(e, e);
        }
        return result;
    }

    /**
     * Получить категории конкретной задачи в виде JSON.
     *
     * @param id идентификатор задачи.
     * @return строка в формате JSON.
     */
    public String getTaskCategoriesAsJson(int id) {
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        TaskDTO task = this.tasksDAO.getTaskById(id);
        List<CategoryDTO> categories = getTaskCategories(task);
        try {
            result = mapper.writeValueAsString(categories);
        } catch (Exception e) {
            LOGGER.info(e, e);
        }
        return result;
    }

    /**
     * Удалить категорию по названию.
     *
     * @param name название категории.
     */
    public void removeCategory(String name) {
        CategoryDTO category;
        try {
            category = this.categoryDAO.getCategoryByName(name);
            if (category != null) {
                this.categoryDAO.deleteCategory(category);
            }
        } catch (UnexistCategoryException e) {
            LOGGER.warn(e, e);
        }
    }
}