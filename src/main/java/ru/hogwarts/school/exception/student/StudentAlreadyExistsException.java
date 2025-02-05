package ru.hogwarts.school.exception.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class StudentAlreadyExistsException extends RuntimeException {

    public static final Logger logger = LoggerFactory.getLogger(StudentAlreadyExistsException.class);

    public static final int CODE = 220;

    public StudentAlreadyExistsException() {

        super("Такой студент уже существует");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
