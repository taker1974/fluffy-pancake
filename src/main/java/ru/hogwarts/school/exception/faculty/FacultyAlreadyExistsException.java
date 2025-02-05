package ru.hogwarts.school.exception.faculty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class FacultyAlreadyExistsException extends RuntimeException {

    public static final Logger logger = LoggerFactory.getLogger(FacultyAlreadyExistsException.class);

    public static final int CODE = 752;

    public FacultyAlreadyExistsException() {

        super("Такой факультет уже существует");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
