package ru.job4j.todo.persistence.exceptions;

/**
 * Исключение, бросающееся в ситуации, когда искомый пользователь не найден в бд.
 */
public class UnexistUserException extends Exception {

    public UnexistUserException(String message) {
        super(message);
    }
}