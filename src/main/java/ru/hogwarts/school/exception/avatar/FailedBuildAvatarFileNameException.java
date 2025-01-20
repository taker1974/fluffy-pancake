package ru.hogwarts.school.exception.avatar;

public class FailedBuildAvatarFileNameException extends RuntimeException {

    public static final int CODE = 649;

    public FailedBuildAvatarFileNameException() {
        super("Не удалось собрать уникальное имя аватара");
    }
}
