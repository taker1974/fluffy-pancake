package ru.hogwarts.school.exception.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class StudentNotFoundException extends RuntimeException {

    public static final Logger logger = LoggerFactory.getLogger(StudentNotFoundException.class);

    public static final int CODE = 784;

    public StudentNotFoundException() {

        super("Студент не найден");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
