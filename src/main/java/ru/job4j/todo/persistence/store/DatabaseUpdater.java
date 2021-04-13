package ru.job4j.todo.persistence.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;

import java.util.Properties;

/**
 * Класс, выполняющий проверку и обновление базы данных приложения.
 */
public class DatabaseUpdater {

    /**
     * Логгер для вывода информации о работе приложения.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Список параметров конфигурации базы данных.
     */
    private Properties config;

    /**
     * Инициализация объекта для обновления базы данных.
     *
     * @param filename название файла конфига.
     */
    public DatabaseUpdater(String filename) {
        this.config = Config.getConfig(filename);
    }

    /**
     * Метод проверки и обновления базы данных приложения.
     */
    public void updateDatabase() {
        String url = this.config.getProperty("jdbc.url");
        String user = this.config.getProperty("jdbc.username");
        String pass = this.config.getProperty("jdbc.password");
        LOGGER.info("Prepare to update database.");
        Flyway flyway = Flyway.configure().dataSource(url, user, pass).load();
        flyway.migrate();
        LOGGER.info("Database update completed.");
    }
}