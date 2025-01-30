package ru.hogwarts.school.controller.advice;

public abstract class AbstractBaseControllerAdvice {

    public static final String MESSAGE_PREFIX = "Перехвачено исключение";

    protected String getCommonMessage(Object obj) {
        return String.format("%s %s", MESSAGE_PREFIX, obj.getClass().getSimpleName());
    }
}
