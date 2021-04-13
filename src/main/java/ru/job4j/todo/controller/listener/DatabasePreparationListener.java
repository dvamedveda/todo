package ru.job4j.todo.controller.listener;


import ru.job4j.todo.persistence.store.DatabaseUpdater;
import ru.job4j.todo.persistence.store.StoreSettings;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Слушатель контекста, выполнящий при запуске приложения проверку и обновление бд при необходимости.
 */
public class DatabasePreparationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(StoreSettings.DB_FILE);
        databaseUpdater.updateDatabase();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}