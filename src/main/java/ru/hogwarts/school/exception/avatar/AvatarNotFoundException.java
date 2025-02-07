package ru.hogwarts.school.exception.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class AvatarNotFoundException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(AvatarNotFoundException.class);

    public static final int CODE = 893;

    public AvatarNotFoundException() {

        super("Аватар не найден");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
