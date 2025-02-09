package ru.hogwarts.school.exception.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class NullStudentException extends RuntimeException {

    public static final Logger logger = LoggerFactory.getLogger(NullStudentException.class);

    public static final int CODE = 807;

    public NullStudentException() {

        super("Параметр Student не должен быть null");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
