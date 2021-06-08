package ru.job4j.todo.persistence;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.exceptions.UnexistCategoryException;
import ru.job4j.todo.persistence.exceptions.UserAlreadyExistException;
import ru.job4j.todo.persistence.models.CategoryDTO;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

import java.sql.Timestamp;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Тесты класса CategoryDAO.
 */
public class CategoryDAOTest {

    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Проверка создания новой категории.
     */
    @Test
    public void whenAddCategoryThenSuccess() {
        CategoryDAO categoryDAO = new CategoryDAO();
        Assert.assertThat(categoryDAO.getAllCategories().size(), is(3));
        CategoryDTO newCategory = new CategoryDTO("Test");
        categoryDAO.addCategory(newCategory);
        Assert.assertThat(categoryDAO.getAllCategories().size(), is(4));
        categoryDAO.deleteCategory(newCategory);
        Assert.assertThat(categoryDAO.getAllCategories().size(), is(3));
    }

    /**
     * Проверка поиска категорий по имени и идентификатору.
     *
     * @throws UnexistCategoryException в случае, если категории не существует.
     */
    @Test
    public void whenSearchCategoriesThenSuccess() throws UnexistCategoryException {
        CategoryDAO categoryDAO = new CategoryDAO();
        CategoryDTO testCategory = new CategoryDTO("Test2");
        categoryDAO.addCategory(testCategory);
        CategoryDTO result1 = categoryDAO.getCategoryById(testCategory.getId());
        Assert.assertEquals(result1, testCategory);
        CategoryDTO result2 = categoryDAO.getCategoryByName(testCategory.getName());
        Assert.assertEquals(result2, testCategory);
        Assert.assertEquals(result1, result2);
        categoryDAO.deleteCategory(testCategory);
    }

    /**
     * Проверка получения списка категорий для задачи.
     *
     * @throws UserAlreadyExistException в случае, когда добавляемый пользователь существует.
     */
    @Test
    public void whenGetTaskCategoriesThenSuccess() throws UserAlreadyExistException {
        CategoryDAO categoryDAO = new CategoryDAO();
        UserDAO userDAO = new UserDAO();
        TasksDAO tasksDAO = new TasksDAO();
        Assert.assertThat(categoryDAO.getAllCategories().size(), is(3));
        CategoryDTO test = new CategoryDTO("Test");
        CategoryDTO urgent = new CategoryDTO("Urgent");
        categoryDAO.addCategory(test);
        categoryDAO.addCategory(urgent);
        UserDTO testUser = userDAO.saveUser(new UserDTO("test@test", "password", "test", new Timestamp(1L)));
        TaskDTO testTask = new TaskDTO("test_description", new Timestamp(1L), false, testUser);
        testTask.addCategory(test);
        testTask.addCategory(urgent);
        tasksDAO.addTask(testTask);
        Assert.assertThat(categoryDAO.getAllCategories().size(), is(5));
        List<CategoryDTO> result = categoryDAO.getTaskCategories(testTask);
        Assert.assertThat(result.size(), is(2));
        Assert.assertThat(result.get(0), is(test));
        Assert.assertThat(result.get(1), is(urgent));
        tasksDAO.deleteTask(testTask);
        userDAO.deleteUser(testUser);
        categoryDAO.deleteCategory(test);
        categoryDAO.deleteCategory(urgent);
    }
}