package ru.job4j.todo.controller;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Вспомогательный класс, для хранения аттрибутов http-запроса и сессии.
 */
public class Answers {

    /**
     * Хранилище аттрибутов.
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     * Класс действия, вызываемого при установке аттрибута.
     */
    public class SetAnswer implements Answer {
        public Void answer(InvocationOnMock invocation) {
            String key = invocation.getArgument(0, String.class);
            Object value = invocation.getArgument(1, Object.class);
            System.out.println("put attribute key=" + key + ", value=" + value);
            attributes.put(key, value);
            return null;
        }
    }

    /**
     * Класс действия, вызываемого при получении аттрибута.
     */
    public class GetAnswer implements Answer {
        public Object answer(InvocationOnMock invocation) {
            String key = invocation.getArgument(0, String.class);
            Object value = attributes.get(key);
            System.out.println("get attribute value for key=" + key + " : " + value);
            return value;
        }
    }
}