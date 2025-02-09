package ru.hogwarts.school.exception.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class BadAvatarFileNameException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(BadAvatarFileNameException.class);

    public static final int CODE = 22;

    public BadAvatarFileNameException(int minFileNameLength, int maxFileNameLength) {

        super(String.format("Имя файла не должно быть null и " +
                "длина имени должна быть от %d до %d символов", minFileNameLength, maxFileNameLength));

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
