package ru.job4j.todo.persistence.models;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Класс для правильной конвертации колонок таблиц в поля объектов.
 * Используется хибернейтом.
 * Обрезает в varchar'ах из бд пробелы при построении объекта.
 */
@Converter
public class Trimmer implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String s) {
        return s;
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return s.trim();
    }
}