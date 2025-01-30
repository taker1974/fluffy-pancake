package ru.hogwarts.school.exception.avatar;

public class BadAvatarSizeException extends RuntimeException {

    public static final int CODE = 677;

    public BadAvatarSizeException(long avatarSizeMin, long avatarSizeMax) {
        super("Размер файла аватара должен быть" +
                " от " + avatarSizeMin +
                " до " + avatarSizeMax + " байт");
    }
}
