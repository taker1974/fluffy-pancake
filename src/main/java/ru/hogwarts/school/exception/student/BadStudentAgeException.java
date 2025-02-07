package ru.hogwarts.school.exception.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class BadStudentAgeException extends RuntimeException {

    public static final Logger logger = LoggerFactory.getLogger(BadStudentAgeException.class);

    public static final int CODE = 527;

    public BadStudentAgeException(int minAge) {

        super(String.format("Возраст студента должен быть не менее %d лет", minAge));

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
