package ru.hogwarts.school.exception.faculty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class FacultyNotFoundException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(FacultyNotFoundException.class);

    public static final int CODE = 682;

    public FacultyNotFoundException() {

        super("Факультет не найден");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
