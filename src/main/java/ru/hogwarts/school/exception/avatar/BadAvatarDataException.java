package ru.hogwarts.school.exception.avatar;

public class BadAvatarDataException extends RuntimeException {

    public static final int CODE = 405;

    public BadAvatarDataException() {
        super("Не удалось прочитать массив байт из аватара.");
    }
}
