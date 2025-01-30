package ru.hogwarts.school.exception.avatar;

public class NullAvatarFileException extends RuntimeException {

    public static final int CODE = 483;

    public NullAvatarFileException() {
        super("Параметр MultipartFile не должен быть null");
    }
}
