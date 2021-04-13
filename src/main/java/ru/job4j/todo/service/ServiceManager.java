package ru.job4j.todo.service;

public class ServiceManager {
    private final TodoService todoService;

    private ServiceManager() {
        todoService = new TodoService();
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
}