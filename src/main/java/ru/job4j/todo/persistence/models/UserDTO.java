package ru.job4j.todo.persistence.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Модель данных для пользователя.
 */
@Entity
@Table(name = "users")
public class UserDTO {

    /**
     * Идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Почта пользователя.
     */
    @Column
    @Convert(converter = Trimmer.class)
    private String email;

    /**
     * Пароль пользователя.
     */
    @Column
    @Convert(converter = Trimmer.class)
    private String password;

    /**
     * Имя пользователя.
     */
    @Column
    @Convert(converter = Trimmer.class)
    private String name;

    /**
     * Время регистрации пользователя.
     */
    @Column
    private Timestamp registered;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getRegistered() {
        return registered;
    }

    public void setRegistered(Timestamp registered) {
        this.registered = registered;
    }

    public UserDTO() {

    };

    public UserDTO(String email, String password, String name, Timestamp registered) {
        this.id = 0;
        this.email = email;
        this.password = password;
        this.name = name;
        this.registered = registered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDTO userDTO = (UserDTO) o;
        return id == userDTO.id
                && Objects.equals(this.getEmail(), userDTO.email)
                && Objects.equals(this.getPassword(), userDTO.password)
                && Objects.equals(this.getName(), userDTO.name)
                && Objects.equals(this.getRegistered(), userDTO.registered);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, registered);
    }
}