package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.job4j.todo.persistence.exceptions.UnexistCategoryException;
import ru.job4j.todo.persistence.models.CategoryDTO;
import ru.job4j.todo.persistence.models.TaskDTO;

import java.util.List;
import java.util.function.Function;

/**
 * Persistense-объект для работы с категориями задач
 */
public class CategoryDAO {

    /**
     * Фабрика сессий hibernate.
     */
    private final SessionFactory sessionFactory;

    /**
     * Инициализация фабрики сессий.
     */
    public CategoryDAO() {
        sessionFactory = SessionFactoryManager.getInstance().getFactory();
    }

    /**
     * Декоратор для выполнения команд хибернейту.
     *
     * @param command лямбда функция для выполнения.
     * @param <T>     результат выполнения лямбды.
     * @return результат работы декоратора.
     */
    private <T> T execute(final Function<Session, T> command) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        try {
            T result = command.apply(session);
            transaction.commit();
            return result;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Получение категорий для конкретной задачи.
     *
     * @param taskDTO задача.
     * @return список категорий.
     */
    public List<CategoryDTO> getTaskCategories(TaskDTO taskDTO) {
        return this.execute(session -> {
            String query = "select distinct t from TaskDTO t join fetch t.categoryDTOList";
            TaskDTO task = (TaskDTO) session.createQuery(query).uniqueResult();
            return task.getCategoryDTOList();
        });
    }

    /**
     * Получить все существующие категории.
     *
     * @return список категорий.
     */
    public List<CategoryDTO> getAllCategories() {
        return this.execute(session -> session.createQuery("from CategoryDTO order by id").list());
    }

    /**
     * Получить объект категории по идентификатору.
     *
     * @param id идентификатор категории.
     * @return объект категории.
     */
    public CategoryDTO getCategoryById(int id) {
        return this.execute(session -> session.find(CategoryDTO.class, id));
    }

    /**
     * Получить объект категории по названию.
     *
     * @param name название категории.
     * @return объект категории.
     * @throws UnexistCategoryException бросается, если категории с таким именем не существует.
     */
    public CategoryDTO getCategoryByName(String name) throws UnexistCategoryException {
        String query = "from CategoryDTO where name = :name";
        CategoryDTO result = this.execute(session -> (CategoryDTO) session.createQuery(query).setParameter("name", name).uniqueResult());
        if (result != null) {
            return result;
        } else {
            throw new UnexistCategoryException("There is not exist category with given name");
        }
    }

    /**
     * Добавить новую категорию.
     *
     * @param categoryDTO объект новой категории.
     * @return сохраненный объект новой категории.
     */
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        return this.execute(session -> {
            session.save(categoryDTO);
            return categoryDTO;
        });
    }

    /**
     * Удалить категорию.
     *
     * @param categoryDTO объект удаляемой категории.
     */
    public void deleteCategory(CategoryDTO categoryDTO) {
        this.execute(session -> {
            session.remove(categoryDTO);
            return true;
        });
    }
}