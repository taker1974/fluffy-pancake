package ru.hogwarts.school.exception.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class IOAvatarFileException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(IOAvatarFileException.class);

    public static final int CODE = 873;

    public IOAvatarFileException() {

        super("Ошибка при работе с файлом аватара");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
