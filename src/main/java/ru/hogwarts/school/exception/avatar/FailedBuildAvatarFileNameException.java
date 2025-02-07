package ru.hogwarts.school.exception.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hogwarts.school.tools.LogEx;

public class FailedBuildAvatarFileNameException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(FailedBuildAvatarFileNameException.class);

    public static final int CODE = 649;

    public FailedBuildAvatarFileNameException() {

        super("Не удалось собрать уникальное имя аватара");

        LogEx.error(logger, LogEx.getThisMethodName(), LogEx.EXCEPTION_THROWN, "CODE = " + CODE, this);
    }
}
