package ru.job4j.todo.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.todo.persistence.exceptions.UserAlreadyExistException;
import ru.job4j.todo.persistence.models.CategoryDTO;
import ru.job4j.todo.persistence.models.TaskDTO;
import ru.job4j.todo.persistence.models.UserDTO;
import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Тесты класса CategoryService.
 */
public class CategoryServiceTest {

    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Проверка поиска категории по названию и идентификатору.
     */
    @Test
    public void whenSearchCategoryThenSuccess() {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        Assert.assertThat(categoryService.getAllCategories().size(), is(3));
        String newName = "newAddedCategory";
        categoryService.createCategory("newAddedCategory");
        Assert.assertThat(categoryService.getAllCategories().size(), is(4));
        CategoryDTO getByName = categoryService.getCategoryByName(newName);
        CategoryDTO getById = categoryService.getCategoryById(getByName.getId());
        Assert.assertEquals(getByName, getById);
        categoryService.removeCategory(newName);
    }

    /**
     * Проверка получения списка всех категорий в формате JSON.
     */
    @Test
    public void whenGetAllCategoriesAsJsonThenSuccess() {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        List<CategoryDTO> categories = categoryService.getAllCategories();
        String result = categoryService.getAllCategoriesAsJson();
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
        Assert.assertThat(result, is(expected));
    }

    /**
     * Проверка получения списка категорий для задачи.
     *
     * @throws UserAlreadyExistException в случае, если добавляемый пользователь уже существует.
     */
    @Test
    public void whenGetTaskCategoriesThenSuccess() throws UserAlreadyExistException {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryDTO categoryHobby = categoryService.getCategoryByName("Хобби");
        List<Integer> userCategories = new ArrayList<>();
        userCategories.add(categoryHobby.getId());
        UserDTO newUser = userService.addNewUser("category@category", "password", "some_name");
        TaskDTO task = todoService.addNewTask("some test task", newUser, userCategories);
        List<CategoryDTO> result = categoryService.getTaskCategories(task);
        Assert.assertThat(result.size(), is(1));
        Assert.assertEquals(result.get(0), categoryHobby);
        todoService.deleteTask(task.getId());
        userService.deleteUser(newUser);
    }

    /**
     * Проверка получения списка категорий для задачи в формате JSON.
     *
     * @throws UserAlreadyExistException в случае, если добавляемый пользователь уже существует.
     */
    @Test
    public void whenGetTaskCategoriesAsJsonThenSuccess() throws UserAlreadyExistException {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryDTO categoryHobby = categoryService.getCategoryByName("Хобби");
        ArrayList<Integer> userCategories = new ArrayList<>();
        userCategories.add(categoryHobby.getId());
        UserDTO newUser = userService.addNewUser("hobby@hobby", "password", "some_name");
        TaskDTO task = todoService.addNewTask("some test task", newUser, userCategories);
        String result = categoryService.getTaskCategoriesAsJson(task.getId());
        String expected = String.format("[{\"id\":%s,\"name\":\"%s\"}]", categoryHobby.getId(), categoryHobby.getName());
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(task.getId());
        userService.deleteUser(newUser);
    }

    /**
     * Проверка удаления категории.
     */
    @Test
    public void whenRemoveCategoryThenSuccess() {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        Assert.assertThat(categoryService.getAllCategories().size(), is(3));
        categoryService.createCategory("NewCategory");
        Assert.assertThat(categoryService.getAllCategories().size(), is(4));
        categoryService.removeCategory("NewCategory");
        Assert.assertThat(categoryService.getAllCategories().size(), is(3));
    }

    /**
     * Проверка удаления несуществующей категории.
     */
    @Test
    public void whenRemoveunexistCategoryThenSuccess() {
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        Assert.assertThat(categoryService.getAllCategories().size(), is(3));
        categoryService.removeCategory("NewCategory");
        Assert.assertThat(categoryService.getAllCategories().size(), is(3));
    }
}