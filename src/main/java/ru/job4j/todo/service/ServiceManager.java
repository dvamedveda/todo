package ru.job4j.todo.service;

public class ServiceManager {
    private final TodoService todoService;
    private final UserService userService;
    private final CategoryService categoryService;

    private ServiceManager() {
        todoService = new TodoService();
        userService = new UserService();
        categoryService = new CategoryService();
    }

    /**
     * Класс реализующий шаблон синглтон для менеджера сервисов.
     */
    private static final class Lazy {
        private static final ServiceManager INST = new ServiceManager();
    }

    /**
     * Метод для получения инстанса менеджера сервисов.
     *
     * @return объект менеджера сервисов.
     */
    public static ServiceManager getInstance() {
        return Lazy.INST;
    }

    /**
     * Получить объект сервиса для работы с todo-сервисом.
     *
     * @return объект todo-сервиса.
     */
    public TodoService getTodoService() {
        return todoService;
    }

    /**
     * Получить объект сервиса для работы с пользователями.
     *
     * @return объект для работы с пользователями.
     */
    public UserService getUserService() {
        return this.userService;
    }

    public CategoryService getCategoryService() {
        return this.categoryService;
    }
}