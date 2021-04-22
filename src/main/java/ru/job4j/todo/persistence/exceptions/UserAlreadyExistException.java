package ru.job4j.todo.persistence.exceptions;

/**
 * Исключение, бросающееся в ситуации, когда в бд записывается уже существующий пользователь.
 */
public class UserAlreadyExistException extends Exception {

    public UserAlreadyExistException(String message) {
        super(message);
    }
}