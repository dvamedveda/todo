package ru.job4j.todo.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Вспомогательный класс, единожды создающий фабрику сессий хибернейт.
 * По запросу выдает готовую фабрику сессий.
 */
public class SessionFactoryManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static SessionFactory sessionFactory;

    public static SessionFactory getFactory() {
        if (sessionFactory == null) {
            initializeFactory();
        }
        return sessionFactory;
    }

    /**
     * Инициализация фабрики сессий.
     */
    private static void initializeFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory sf = null;
        try {
            sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            LOGGER.error("Can't configure hibernate registry! Registry will be destroyed.");
            LOGGER.error(e, e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
        LOGGER.info("Hibernate session factory initialized successfully.");
        sessionFactory = sf;
    }
}