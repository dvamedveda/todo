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

import static org.hamcrest.CoreMatchers.is;

/**
 * Тесты класса TodoService.
 */
public class TodoServiceTest {

    /**
     * Преварительная проверка и обновление тестовой базы данных.
     */
    @BeforeClass
    public static void setUp() {
        DatabaseUpdater updater = new DatabaseUpdater(StoreSettings.TEST_DB_FILE);
        updater.updateDatabase();
    }

    /**
     * Проверка добавления задачи на слое сервиса.
     */
    @Test
    public void whenAddTaskThenSuccess() throws UserAlreadyExistException {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        List<Integer> categoryList = new ArrayList<>();
        categoryList.add(category.getId());
        UserDTO newUser = userService.addNewUser("test1@test1", "password", "some_name");
        TaskDTO task = todoService.addNewTask("some test task", newUser, categoryList);
        TaskDTO result = todoService.getTaskById(task.getId());
        Assert.assertThat(result.getId(), is(task.getId()));
        Assert.assertThat(result.getDescription(), is("some test task"));
        Assert.assertThat(result.getCreated(), is(task.getCreated()));
        Assert.assertThat(result.isDone(), is(false));
        Assert.assertEquals(result.getUser(), newUser);
        todoService.deleteTask(result.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
    }

    /**
     * Проверка изменения статуса задачи на слое сервиса.
     */
    @Test
    public void whenCheckTaskThenSuccess() throws UserAlreadyExistException {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        List<Integer> categoryList = new ArrayList<>();
        categoryList.add(category.getId());
        UserDTO newUser = userService.addNewUser("test2@test2", "password", "some_name");
        TaskDTO task = todoService.addNewTask("new test task", newUser, categoryList);
        todoService.checkTask(task.getId(), true);
        TaskDTO result = todoService.getTaskById(task.getId());
        Assert.assertThat(result.getId(), is(task.getId()));
        Assert.assertThat(result.getDescription(), is("new test task"));
        Assert.assertThat(result.getCreated(), is(task.getCreated()));
        Assert.assertThat(result.isDone(), is(true));
        Assert.assertEquals(result.getUser(), newUser);
        todoService.deleteTask(result.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
    }

    /**
     * Проверка получения списка всех задача на слое сервиса.
     */
    @Test
    public void whenGetAllTasksThenCorrect() throws UserAlreadyExistException {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        List<Integer> categoryList = new ArrayList<>();
        categoryList.add(category.getId());
        UserDTO newUser = userService.addNewUser("test3@test3", "password", "some_name");
        TaskDTO taskCompleted = todoService.addNewTask("completed task", newUser, categoryList);
        TaskDTO taskIncompleted = todoService.addNewTask("incompleted task", newUser, categoryList);
        todoService.checkTask(taskCompleted.getId(), true);
        int completedId = taskCompleted.getId();
        long completedTime = taskCompleted.getCreated().getTime();
        int incompletedId = taskIncompleted.getId();
        long incompletedTime = taskIncompleted.getCreated().getTime();
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"incompleted task\",", incompletedId))
                .append(String.format("\"created\":%s,\"done\":false,", incompletedTime))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s},", newUser.getRegistered().getTime()))
                .append(String.format("\"categoryDTOList\":[{\"id\":%s,\"name\":\"%s\"}]}", category.getId(), category.getName()))
                .append(",")
                .append(String.format("{\"id\":%s,\"description\":\"completed task\",", completedId))
                .append(String.format("\"created\":%s,\"done\":true,", completedTime))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s},", newUser.getRegistered().getTime()))
                .append(String.format("\"categoryDTOList\":[{\"id\":%s,\"name\":\"%s\"}]}", category.getId(), category.getName()))
                .append("]")
                .toString();
        String result = todoService.getAllTasksAsJson();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(taskCompleted.getId());
        todoService.deleteTask(taskIncompleted.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
    }

    /**
     * Проверка получения списка незавершенных задач на слое сервиса.
     */
    @Test
    public void whenGetIncompletedTasksThenCorrect() throws UserAlreadyExistException {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        List<Integer> categoryList = new ArrayList<>();
        categoryList.add(category.getId());
        UserDTO newUser = userService.addNewUser("test4@test4", "password", "some_name");
        TaskDTO taskCompleted = todoService.addNewTask("test completed task", newUser, categoryList);
        TaskDTO taskIncompleted = todoService.addNewTask("test incompleted task", newUser, categoryList);
        todoService.checkTask(taskCompleted.getId(), true);
        int incompletedId = taskIncompleted.getId();
        long incompletedTime = taskIncompleted.getCreated().getTime();
        String expected = new StringBuilder()
                .append("[")
                .append(String.format("{\"id\":%s,\"description\":\"test incompleted task\",", incompletedId))
                .append(String.format("\"created\":%s,\"done\":false,", incompletedTime))
                .append("\"user\":{")
                .append(String.format("\"id\":%s,\"email\":\"%s\",", newUser.getId(), newUser.getEmail()))
                .append(String.format("\"password\":\"%s\",\"name\":\"%s\",", newUser.getPassword(), newUser.getName()))
                .append(String.format("\"registered\":%s},", newUser.getRegistered().getTime()))
                .append(String.format("\"categoryDTOList\":[{\"id\":%s,\"name\":\"%s\"}]}", category.getId(), category.getName()))
                .append("]")
                .toString();
        String result = todoService.getIncompleteTasksAsJson();
        Assert.assertThat(result, is(expected));
        todoService.deleteTask(taskCompleted.getId());
        todoService.deleteTask(taskIncompleted.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
    }

    /**
     * Проверка удаления тасков пользователя.
     */
    @Test
    public void whenDeleteUserTasksThenSuccess() throws UserAlreadyExistException {
        TodoService todoService = ServiceManager.getInstance().getTodoService();
        UserService userService = ServiceManager.getInstance().getUserService();
        CategoryService categoryService = ServiceManager.getInstance().getCategoryService();
        CategoryDTO category = categoryService.createCategory("some_category");
        List<Integer> categoryList = new ArrayList<>();
        categoryList.add(category.getId());
        UserDTO newUser = userService.addNewUser("test4@test4", "password", "some_name");
        todoService.addNewTask("test completed task1", newUser, categoryList);
        todoService.addNewTask("test incompleted task2", newUser, categoryList);
        todoService.deleteUserTasks(newUser.getId());
        userService.deleteUser(newUser);
        categoryService.removeCategory(category.getName());
        Assert.assertThat(todoService.getAllTasksAsJson(), is("[]"));
    }
}