package ru.hogwarts.school.exception.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class BadAvatarDataException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(BadAvatarDataException.class);

    public static final int CODE = 405;

    public BadAvatarDataException() {

        super("Не удалось прочитать массив байт из аватара.");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
