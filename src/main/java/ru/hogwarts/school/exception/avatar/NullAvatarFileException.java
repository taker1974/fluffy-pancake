package ru.hogwarts.school.exception.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class NullAvatarFileException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(NullAvatarFileException.class);

    public static final int CODE = 483;

    public NullAvatarFileException() {

        super("Параметр MultipartFile не должен быть null");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
