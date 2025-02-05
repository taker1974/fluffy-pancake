package ru.hogwarts.school.exception.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class BadAvatarSizeException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(BadAvatarSizeException.class);

    public static final int CODE = 677;

    public BadAvatarSizeException(long avatarSizeMin, long avatarSizeMax) {

        super("Размер файла аватара должен быть" +
                " от " + avatarSizeMin +
                " до " + avatarSizeMax + " байт");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
