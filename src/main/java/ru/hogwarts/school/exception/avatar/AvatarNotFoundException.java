package ru.hogwarts.school.exception.avatar;

public class AvatarNotFoundException extends RuntimeException {

    public static final int CODE = 893;

    public AvatarNotFoundException() {

        super("Аватар не найден");
    }
}
