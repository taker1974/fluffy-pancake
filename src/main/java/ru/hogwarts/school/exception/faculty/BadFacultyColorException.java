package ru.hogwarts.school.exception.faculty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class BadFacultyColorException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(BadFacultyColorException.class);

    public static final int CODE = 195;

    public BadFacultyColorException(int minColorLength, int maxColorLength) {

        super(String.format(
                "\"Цвет\" факультета не должен быть null и " +
                "длина этой строки должна быть от %d до %d символов", minColorLength, maxColorLength));

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
