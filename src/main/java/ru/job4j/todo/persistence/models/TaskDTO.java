package ru.job4j.todo.persistence.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Модель данных для задачи.
 */
@Entity
@Table(name = "tasks")
public class TaskDTO {

    /**
     * Идентификатор задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Описание задачи.
     */
    @Column
    private String description;

    /**
     * Время создания задачи.
     */
    @Column
    private Timestamp created;

    /**
     * Флаг выполнения задачи.
     */
    @Column
    private boolean done;

    /**
     * Юзер-хозяин задачи.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDTO user;

    /**
     * Список категорий задачи, может быть пустым.
     * Категории в списке отсортированы по возрастанию id.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "categories_tasks",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @OrderBy("id asc")
    private List<CategoryDTO> categoryDTOList = new ArrayList<>();

    public TaskDTO() {
    }

    public TaskDTO(String description, Timestamp created, boolean done, UserDTO userDTO) {
        this.id = 0;
        this.description = description;
        this.created = created;
        this.done = done;
        this.user = userDTO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<CategoryDTO> getCategoryDTOList() {
        return categoryDTOList;
    }

    public void addCategory(CategoryDTO categoryDTO) {
        this.categoryDTOList.add(categoryDTO);
    }
}