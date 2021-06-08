package ru.job4j.todo.persistence.exceptions;

/**
 * Исключение, бросающееся в случае попытки работы с несуществующей категорией.
 */
public class UnexistCategoryException extends Exception {

    public UnexistCategoryException(String message) {
        super(message);
    }
}