package ru.job4j.todo.persistence.store;

/**
 * Вспомогательный класс, хранящий названия конфигов бд для разных целей.
 */
public class StoreSettings {
    /**
     * Конфиг боевой базы данных.
     */
    public final static String DB_FILE = "db.properties";

    /**
     * Конфиг тестовой базы данных.
     */
    public final static String TEST_DB_FILE = "test_db.properties";
}