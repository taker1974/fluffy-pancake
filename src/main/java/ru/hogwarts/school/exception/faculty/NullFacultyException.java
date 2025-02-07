package ru.hogwarts.school.exception.faculty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class NullFacultyException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(NullFacultyException.class);

    public static final int CODE = 141;

    public NullFacultyException() {

        super("Параметр Faculty не должен быть null");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
