package ru.job4j.todo.persistence.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс, инкапсулирующий загрузку конфига базы данных.
 */
public class Config {

    /**
     * Логгер для вывода информации о работе приложения.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Объект для хранения информации, загруженной из файла конфигурации базы данных.
     */
    private static Properties properties = new Properties();

    /**
     * Получить данные конфигурации базы данных.
     *
     * @param configFile имя файла конфигурации.
     * @return объект содержащий список параметров конфигурации.
     */
    public static Properties getConfig(String configFile) {
        loadConfig(configFile);
        return properties;
    }

    /**
     * Загрузить список параметров конфигаруции из файла.
     *
     * @param filename имя файла конфигурации.
     */
    private static void loadConfig(String filename) {
        try (InputStream configFile = Config.class.getClassLoader().getResourceAsStream(filename)) {
            properties.load(configFile);
            LOGGER.info("Config loaded.");
            LOGGER.info("JDBC url is: " + properties.getProperty("jdbc.url"));
            LOGGER.info("JDBC username is: " + properties.getProperty("jdbc.username"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}