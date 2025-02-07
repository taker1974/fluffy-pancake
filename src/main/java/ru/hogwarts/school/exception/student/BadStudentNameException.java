package ru.hogwarts.school.exception.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class BadStudentNameException extends RuntimeException {

    public static final Logger logger = LoggerFactory.getLogger(BadStudentNameException.class);

    public static final int CODE = 153;

    public BadStudentNameException(int minNameLength, int maxNameLength) {

        super(String.format("Имя студента не должно быть null и " +
                "длина имени должна быть от %d до %d символов", minNameLength, maxNameLength));

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
