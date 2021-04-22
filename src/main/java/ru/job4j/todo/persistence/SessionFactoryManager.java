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
    private final SessionFactory sessionFactory;

    private SessionFactoryManager() {
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

    private static final class Lazy {
        private static final SessionFactoryManager INST = new SessionFactoryManager();
    }

    public static SessionFactoryManager getInstance() {
        return Lazy.INST;
    }

    /**
     * Получить фабрику сессий.
     *
     * @return фабрика сессий.
     */
    public SessionFactory getFactory() {
        return this.sessionFactory;
    }
}