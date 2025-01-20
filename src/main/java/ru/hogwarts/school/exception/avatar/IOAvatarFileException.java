package ru.hogwarts.school.exception.avatar;

public class IOAvatarFileException extends RuntimeException {

    public static final int CODE = 873;

    public IOAvatarFileException() {
        super("Ошибка при работе с файлом аватара");
    }
}
