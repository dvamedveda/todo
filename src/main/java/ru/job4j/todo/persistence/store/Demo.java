package ru.job4j.todo.persistence.store;

/**
 * Класс для демонстрации обновления базы данных.
 */
public class Demo {
    public static void main(String[] args) {
        DatabaseUpdater updater = new DatabaseUpdater("db.properties");
        updater.updateDatabase();
    }
}