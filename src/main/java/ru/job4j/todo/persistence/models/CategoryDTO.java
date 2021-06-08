package ru.job4j.todo.persistence.models;

import javax.persistence.*;
import java.util.Objects;

/**
 * Модель данных для категории задач.
 */
@Entity
@Table(name = "categories")
public class CategoryDTO {

    /**
     * Идентификатор категории.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Название категории.
     */
    private String name;

    public CategoryDTO() {

    }

    public CategoryDTO(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryDTO that = (CategoryDTO) o;
        return id == that.id
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}