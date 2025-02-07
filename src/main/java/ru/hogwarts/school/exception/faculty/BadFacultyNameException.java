package ru.hogwarts.school.exception.faculty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class BadFacultyNameException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(BadFacultyNameException.class);

    public static final int CODE = 390;

    public BadFacultyNameException(int minNameLength, int maxNameLength) {

        super(String.format(
                "Название факультета не должно быть null и " +
                "длина названия должна быть от %d до %d символов", minNameLength, maxNameLength));

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
