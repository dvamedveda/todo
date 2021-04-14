package ru.job4j.todo.persistence.models;

import javax.persistence.*;
import java.sql.Timestamp;


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

    public TaskDTO() {
    }

    public TaskDTO(String description, Timestamp created, boolean done) {
        this.id = 0;
        this.description = description;
        this.created = created;
        this.done = done;
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
}